package com.sherlock.gmall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/2 22:15
 * @Description:  商品确认VO
 */
public class OrderConfirmVo {

    // 收货地址
    @Setter @Getter
    private List<MemberAddressVo> addresses;

    // 所有选中的购物项
    @Setter @Getter
    private List<OrderItemVo> items;

    // 发票记录。。。

    // 优惠券信息。。。

    // 会员积分
    @Setter @Getter
    private Integer integration;

    // 订单总额
    // private BigDecimal total;

    // 应付价格
    // private BigDecimal payPrice;

    public Integer getCount(){
        Integer count = 0;
        if (!CollectionUtils.isEmpty(items)){
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }


    /**
     * 订单的防重令牌
     */
    @Getter @Setter
    private String orderToken;

    public BigDecimal getTotal() {
        BigDecimal totalPrice = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(items)){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                totalPrice = totalPrice.add(multiply);
            }
        }
        return totalPrice;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
