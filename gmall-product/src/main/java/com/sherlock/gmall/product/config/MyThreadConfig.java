package com.sherlock.gmall.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @auther Sherlock
 * @date 2020/8/10 22:13
 * @Description:
 */
@Configuration
// @EnableConfigurationProperties(ThreadPoolConfigProperties.class)
public class MyThreadConfig {

    /**
     * 自定义线程池 放入容器
     *
     * 可以使用 @EnableConfigurationProperties(ThreadPoolConfigProperties.class)开启该类的配置
     * 或者在ThreadPoolConfigProperties头上标注@Component放入容器 就可以直接在方法参数中使用
     * @return
     */

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties poolConfigProperties) {
        return new ThreadPoolExecutor(poolConfigProperties.getCorePoolSize(),poolConfigProperties.getMaximumPoolSize(),
                poolConfigProperties.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

}
