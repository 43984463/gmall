package com.sherlock.gmall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @link
 * @auther Sherlock
 * @date 2020/7/12 23:59
 * @Description:
 *
 *
 * https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95  redisson github百科
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    private final static String REDIS_PREFIX = "redis://";
    private final static String COLON = ":";

    /**
     * 集群模式
     * @return
     */
    /*@Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = new Config();
        config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://127.0.0.1:7000", "redis://127.0.0.1:7001")
                .addNodeAddress("redis://127.0.0.1:7002");

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }*/

    /**
     * 单点模式
     * @return
     */
    @Bean
    public RedissonClient redissonClient() throws IOException {
        // 默认连接地址 127.0.0.1:6379
        // RedissonClient redisson = Redisson.create();

        // 单节点模式并且设置连接地址
        Config config = new Config();
        config.useSingleServer()
                //.setAddress("redis://192.168.1.4:6379")
                .setAddress(REDIS_PREFIX + redisHost + COLON + redisPort)
                // redis连接密码
                .setPassword(redisPassword);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
