package com.sherlock.common.exception;

/**
 * @auther Sherlock
 * @date 2020/9/5 20:54
 * @Description:
 */
public class NoStockException extends RuntimeException {

    private Long skuId;

    public NoStockException(Long skuId){
        super("商品Id: " +skuId+ "没有足够的库存");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
