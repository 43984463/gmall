package com.sherlock.gmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.vo.SpuInfoVo;
import com.sherlock.gmall.product.entity.SpuInfoEntity;
import com.sherlock.gmall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-08 23:38:29
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuInfo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);

    SpuInfoVo getSpuInfoBySkuId(Long skuId);
}

