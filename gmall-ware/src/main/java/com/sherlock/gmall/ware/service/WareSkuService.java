package com.sherlock.gmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.gmall.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:18:07
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateStock(Long skuId, Long wareId, Integer skuNum);
}

