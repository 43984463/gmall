package com.sherlock.gmall.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @auther Sherlock
 * @date 2020/9/10 0:13
 * @Description: 只是为了开启AOP的代理
 */
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class AopConfig {
}
