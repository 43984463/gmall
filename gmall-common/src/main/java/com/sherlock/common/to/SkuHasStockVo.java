package com.sherlock.common.to;

import lombok.Data;

/**
 * @auther Sherlock
 * @date 2020/6/17 21:08
 * @Description:
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;
}
