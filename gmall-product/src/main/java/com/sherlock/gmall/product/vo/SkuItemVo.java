package com.sherlock.gmall.product.vo;

import com.sherlock.gmall.product.entity.SkuImagesEntity;
import com.sherlock.gmall.product.entity.SkuInfoEntity;
import com.sherlock.gmall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/8/8 21:23
 * @Description:
 */
@Data
public class SkuItemVo {

    // 1、sku基本信息  pms_sku_info
    private SkuInfoEntity info;

    private boolean hasStock = true;

    // 2、sku的图片信息 pms_sku_images
    private List<SkuImagesEntity> images;
    // 3、获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;
    // 4、获取spu的介绍
    private SpuInfoDescEntity desc;
    // 5、获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;

}