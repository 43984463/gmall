package com.sherlock.gmall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @auther Sherlock
 * @date 2020/9/12 11:23
 * @Description: 开启定时任务和异步执行
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ScheduledConfig {
}
