package com.sherlock.gmall.order.to;

import com.sherlock.gmall.order.entity.OrderEntity;
import com.sherlock.gmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/4 20:45
 * @Description:
 */
@Data
public class OrderCreateTo {

    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice;

    private BigDecimal fare;

}
