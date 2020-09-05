package com.sherlock.common.vo;

import lombok.Data;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/5 20:17
 * @Description:
 */
@Data
public class WareSkuLockVo {

    private String orderSn; // 需要锁定的订单
    private List<OrderItemVo> locks; // 需要锁住的所有商品

}
