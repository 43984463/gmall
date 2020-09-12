package com.sherlock.gmall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sherlock.common.constants.GmallSeckillConstant;
import com.sherlock.common.exception.GmallHttpStatus;
import com.sherlock.common.to.SecKillSkuRedisTo;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.SkuInfoVo;
import com.sherlock.gmall.seckill.feign.CouponFeignService;
import com.sherlock.gmall.seckill.feign.ProductFeignService;
import com.sherlock.gmall.seckill.service.SeckillService;
import com.sherlock.gmall.seckill.vo.SeckillSessionsWithSkus;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @auther Sherlock
 * @date 2020/9/12 11:34
 * @Description:
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        // 扫描需要上架的秒杀商品
        R session = couponFeignService.getLates3DaySession();
        if (session.getCode() == GmallHttpStatus.RESPONSE_OK) {
            List<SeckillSessionsWithSkus> data = (List<SeckillSessionsWithSkus>) session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            if (!CollectionUtils.isEmpty(data)) {
                // 缓存到redis
                // 1、缓存活动信息
                saveSessionInfos(data);
                // 2、缓存活动的关联商品信息
                saveSessionSkuInfos(data);
            }
        }
    }

    /**
     * 获取当前可以参与的秒杀商品的信息
     *
     * @return
     */
    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        // 1、获取当前时间的所有秒杀场次
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(GmallSeckillConstant.SESSIONS_CACHE_PREFIX + "*");
        for (String key : keys) {
            String replace = key.replace(GmallSeckillConstant.SESSIONS_CACHE_PREFIX, "");
            String[] split = replace.split("_");
            Long start = Long.parseLong(split[0]);
            Long end = Long.parseLong(split[1]);
            if (time >= start && time <= end) {
                // 获取当前场次需要的所有商品信息
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                if (!CollectionUtils.isEmpty(range)) {
                    BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(GmallSeckillConstant.SKUKILL_SESSIONS_CACHE_PREFIX);
                    List<String> multiGet = operations.multiGet(range);
                    if (!CollectionUtils.isEmpty(multiGet)) {
                        List<SecKillSkuRedisTo> collect = multiGet.stream().map(item -> JSON.parseObject(item, SecKillSkuRedisTo.class)).collect(Collectors.toList());
                        return collect;
                    }
                }
                break;
            }
        }
        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        // 1、找到所有的需要参与秒杀的商品的key
        BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(GmallSeckillConstant.SKUKILL_SESSIONS_CACHE_PREFIX);
        Set<String> keys = operations.keys();
        String regx = "\\d" + "_" + skuId;
        if (!CollectionUtils.isEmpty(keys)) {
            for (String key : keys) {
                // 和当前的传入的ID进行匹配，找到第一个就return， 因为是匹配到的第一个
                if (Pattern.matches(regx, key)) {
                    String json = operations.get(key);
                    SecKillSkuRedisTo redisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
                    long time = new Date().getTime();
                    long startTime = redisTo.getStartTime();
                    long endTime = redisTo.getEndTime();
                    // 不在秒杀时间内时不返回随机秒杀码
                    if (time < startTime || time > endTime) {
                        redisTo.setRandomCode(null);
                    }
                    return redisTo;
                }
            }
        }
        return null;
    }

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions) {
        sessions.forEach(session -> {
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = GmallSeckillConstant.SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean hasKey = redisTemplate.hasKey(key);
            // 保持幂等性，不重复往redis里面存储
            if (!hasKey) {
                // 场次ID + SKU id
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "_" + item.getSkuId()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions) {
        // 准备redis的hash操作
        BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(GmallSeckillConstant.SKUKILL_SESSIONS_CACHE_PREFIX);
        sessions.forEach(session -> {
            session.getRelationSkus().forEach(seckillSkuVo -> {
                Boolean hasSku = operations.hasKey(seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId());
                if (!hasSku) {

                    // 缓存商品
                    SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();

                    //1、 sku的基本信息
                    R<SkuInfoVo> skuInfo = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == GmallHttpStatus.RESPONSE_OK) {
                        SkuInfoVo skuInfoVo = skuInfo.getData(new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfo(skuInfoVo);
                    }

                    //2、 sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);

                    //3、 设置商品的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    //4、 设置商品的随机码
                    String randomToken = UUID.randomUUID().toString().replace("-", "");
                    redisTo.setRandomCode(randomToken);

                    //5、 放入缓存(上架商品)
                    String S = JSON.toJSONString(redisTo);
                    operations.put(seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId(), S);

                    //6、 引入分布式信号量  使用库存数量作为分布式信号量的数量 作用就是限流(限制数量) key引入随机码是为了在页面展示的时候带上这个，防止别人恶意抢商品，但是如果没有这个随机key就不能抢
                    // 如果当前场次的商品的库存信息已经上架那么就不需要再次上架了
                    RSemaphore semaphore = redissonClient.getSemaphore(GmallSeckillConstant.SKU_STOCK_SEMAPHORE_PREFIX + randomToken);
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
                }
            });
        });
    }


}
