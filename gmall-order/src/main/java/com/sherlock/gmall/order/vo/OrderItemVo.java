package com.sherlock.gmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/2 22:22
 * @Description:
 */
@Data
public class OrderItemVo {

    private Long skuId;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Integer count = 0;

}
