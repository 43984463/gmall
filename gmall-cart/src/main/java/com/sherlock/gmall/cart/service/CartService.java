package com.sherlock.gmall.cart.service;

import com.sherlock.gmall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @auther Sherlock
 * @date 2020/8/28 21:38
 * @Description:
 */
public interface CartService {
    /**
     * 把商品添加到购物车
     * @param skuId
     * @param num
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车中的某个购物项
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

}
