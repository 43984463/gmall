package com.sherlock.gmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author xueshuai
 * @Description: 订单提交的数据
 * @Date 2020/9/4
 **/
@Data
public class OrderSubmitVo {

    private Long addrId; // 收货地址
    private Integer payType; // 支付方式
    // 无需提交需要购买的商品，提交的时候重新去购物车获取
    // 优惠， 发票
    private String orderToken; // 防重令牌
    private BigDecimal payPrice; // 应付价格 用来验价
    private String note; // 订单备注

    // 用户相关信息 直接从session中获取

}
