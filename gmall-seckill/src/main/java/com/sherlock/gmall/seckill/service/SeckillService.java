package com.sherlock.gmall.seckill.service;

import com.sherlock.common.to.SecKillSkuRedisTo;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/12 11:33
 * @Description:
 */
public interface SeckillService {
    void uploadSeckillSkuLatest3Days();

    List<SecKillSkuRedisTo> getCurrentSeckillSkus();

    SecKillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
