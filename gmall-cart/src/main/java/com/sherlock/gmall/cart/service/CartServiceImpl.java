package com.sherlock.gmall.cart.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @auther Sherlock
 * @date 2020/8/28 21:39
 * @Description:
 */

@Slf4j
@Service("cartService")
public class CartServiceImpl {

    @Autowired
    private StringRedisTemplate redisTemplate;

}
