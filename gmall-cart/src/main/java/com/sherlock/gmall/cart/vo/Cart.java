package com.sherlock.gmall.cart.vo;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/8/28 21:14
 * @Description: 购物车
 */

public class Cart {

    private List<CartItem> items;
    private Integer countNum;  //商品数量
    private Integer countType; //商品类型数量
    private BigDecimal totalAmount; //商品总价 - 减免价格
    private BigDecimal reduce = new BigDecimal(0); //减免价格

    public List<CartItem> getItems() {
        return items;
    }

    public Cart setItems(List<CartItem> items) {
        this.items = items;
        return this;
    }

    public Integer getCountNum() {
        int countNum = 0;
        if (!CollectionUtils.isEmpty(items)) {
            for (CartItem item : items) {
                countNum += item.getCount();
            }
        }
        return countNum;
    }

    public Integer getCountType() {
        int countType = 0;
        if (!CollectionUtils.isEmpty(items)) {
            for (CartItem item : items) {
                countType += 1;
            }
        }
        return countType;
    }

    public BigDecimal getTotalAmount() {
        // 计算购物项的总价
        BigDecimal amount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(items)) {
            for (CartItem item : items) {
                // 只计算被选中的商品
                if (item.isCheck()) {
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        // 减去优惠总价
        BigDecimal subtract = amount.subtract(getReduce());
        return subtract;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public Cart setReduce(BigDecimal reduce) {
        this.reduce = reduce;
        return this;
    }
}
