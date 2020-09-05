package com.sherlock.gmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.LockStockResultVo;
import com.sherlock.common.vo.OrderItemVo;
import com.sherlock.common.vo.SkuInfoVo;
import com.sherlock.common.vo.WareSkuLockVo;
import com.sherlock.gmall.ware.dao.WareSkuDao;
import com.sherlock.gmall.ware.entity.WareSkuEntity;
import com.sherlock.gmall.ware.exception.NoStockException;
import com.sherlock.gmall.ware.feign.ProductFeignService;
import com.sherlock.gmall.ware.service.WareSkuService;
import com.sherlock.common.to.SkuHasStockVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private WareSkuDao wareSkuDao;

    @Resource
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(skuId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateStock(Long skuId, Long wareId, Integer skuNum) {
        WareSkuEntity entity = getOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entity == null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStockLocked(0);
            R<SkuInfoVo> info = productFeignService.getSkuInfo(skuId);
            // 远程获取失败不影响整个商品信息保存
            try {
                if (info.getCode() == 0) {
                    //Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    wareSkuEntity.setSkuName(skuInfo.getSkuName());
                }
            } catch (Exception e) {
                log.warn("远程获取商品信息失败");
            }
            save(wareSkuEntity);
        } else {
            wareSkuDao.updateStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            long count = wareSkuDao.getSkuSotck(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count > 0);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 锁定商品
     * @param lockVo
     * @return
     *
     * 默认运行时异常全都回滚
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo lockVo) {
        // TODO 1、按照下单地址，找到附近仓库，锁定库存
        List<OrderItemVo> locks = lockVo.getLocks();

        // 1、找到每个商品在哪个仓库都有库存
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            // 查询哪个库存包含这个商品
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        Boolean allLocked = true;
        // 2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuLocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (CollectionUtils.isEmpty(wareIds)) {
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                // 成功返回1 (更新数据库一条数据，当前商品在这个仓库中有并且被锁住了) 否则为0
               long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
               if (count == 1) {
                   skuLocked = true;
                   break;
               } else {
                   // 当前仓库锁失败(没有足够的货)，重试下一个仓库

               }
            }
            if (skuLocked == false) {
                // 当前商品在所有仓库中都没有被锁住(货源不足)
                throw new NoStockException(skuId);
            }
        }

        // 代码走到这说明全都锁定成功

        return true;
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}