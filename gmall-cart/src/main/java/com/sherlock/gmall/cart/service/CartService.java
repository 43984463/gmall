package com.sherlock.gmall.cart.service;

import com.sherlock.gmall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @auther Sherlock
 * @date 2020/8/28 21:38
 * @Description:
 */
public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;
}
