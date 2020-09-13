package com.sherlock.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @auther Sherlock
 * @date 2020/9/13 13:58
 * @Description:
 */
@Data
public class SeckillOrderTo {

    private String orderSn;
    /**
     * 场次ID
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 购买数量
     */
    private Integer num;
    /**
     * 会员ID
     */
    private Long mumberId;

}
