package com.sherlock.gmall.order;

/**
 * @auther Sherlock
 * @date 2020/5/8 21:49
 * @Description: gmall-order
 * <p>
 * 使用rabbitmq
 * 1、引入amqp场景(导入依赖)
 *      <dependency>
 *           <groupId>org.springframework.boot</groupId>
 *           <artifactId>spring-boot-starter-amqp</artifactId>
 *      </dependency>
 *
 * @see org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration 就会自动生效， 给容器中自动配置了
 * @see org.springframework.amqp.rabbit.core.RabbitTemplate
 * @see org.springframework.amqp.core.AmqpAdmin
 * @see org.springframework.amqp.rabbit.connection.CachingConnectionFactory
 * @see org.springframework.amqp.rabbit.core.RabbitMessagingTemplate
 *
 * 2、@EnableRabbit
 * 3、{@link org.springframework.boot.autoconfigure.amqp.RabbitProperties} 有所有关于 rabbitmq 的配置信息
 *
 *
 */

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class GmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallOrderApplication.class, args);
    }

}
