package com.sherlock.gmall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/8/28 21:14
 * @Description: 购物项
 */
public class CartItem {

    private Long skuId;
    private boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Integer count;

    public Long getSkuId() {
        return skuId;
    }

    public CartItem setSkuId(Long skuId) {
        this.skuId = skuId;
        return this;
    }

    public boolean isCheck() {
        return check;
    }

    public CartItem setCheck(boolean check) {
        this.check = check;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CartItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getImage() {
        return image;
    }

    public CartItem setImage(String image) {
        this.image = image;
        return this;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public CartItem setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public CartItem setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(count));
    }

    public CartItem setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public CartItem setCount(Integer count) {
        this.count = count;
        return this;
    }
}
