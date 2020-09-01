package com.sherlock.gmall.order.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/9/1
 **/
@EnableRabbit
@Configuration
public class RabbitMQConfig {
    /**
    * 设置rabbitmq发送的消息使用json形式
    */
    private MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
