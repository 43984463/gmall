package com.sherlock.common.vo;

import lombok.Data;

/**
 * @auther Sherlock
 * @date 2020/9/5 20:24
 * @Description:
 *
 * @see com.sherlock.gmall.ware.controller.WareSkuController#orderLockStock(com.sherlock.common.vo.WareSkuLockVo)
 */
@Data
public class LockStockResultVo {

    private Long skuId;
    private Integer num;
    private boolean locked;

}
