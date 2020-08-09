package com.sherlock.gmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/8/9 12:57
 * @Description:
 */
@Data
@ToString
public class SkuItemSaleAttrVo {

    private Long attrId;

    private String attrName;

    private List<AttrValueWithSkuIdVo> attrValues;

}
