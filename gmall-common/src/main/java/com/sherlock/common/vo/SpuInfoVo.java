package com.sherlock.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @auther Sherlock
 * @date 2020/9/5 0:31
 * @Description:
 *
 * @see com.sherlock.gmall.product.controller.SpuInfoController#getSpuInfoBySkuId(java.lang.Long)
 */
@Data
public class SpuInfoVo {

    private Long id;
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 所属分类id
     */
    private Long catalogId;
    /**
     * 品牌id
     */
    private Long brandId;

    private String brandName;
    /**
     *
     */
    private BigDecimal weight;
    /**
     * 上架状态[0 - 下架，1 - 上架]
     */
    private Integer publishStatus;
    /**
     *
     */
    private Date createTime;
    /**
     *
     */
    private Date updateTime;

    private BigDecimal skuPrice;
}
