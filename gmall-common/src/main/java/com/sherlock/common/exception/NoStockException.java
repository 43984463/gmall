package com.sherlock.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @auther Sherlock
 * @date 2020/9/5 20:54
 * @Description:
 */
public class NoStockException extends RuntimeException {

    @Getter @Setter
    private Long skuId;

    @Getter @Setter
    private String msg;

    public NoStockException(){

    }

    public NoStockException(Long skuId){
        super("商品Id: " +skuId+ "没有足够的库存");
        this.msg = "商品Id: " +skuId+ "没有足够的库存";
        this.skuId = skuId;
    }

    public NoStockException(String message){
        super(message);
        this.msg = message;
    }

}
