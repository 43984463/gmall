package com.sherlock.gmall.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @auther Sherlock
 * @date 2020/8/28 21:37
 * @Description:
 */
@Configuration
public class RedisConfig {

    /**
     * 设置redis序列化机制   使用json进行序列化存储
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

}
