package com.sherlock.gmall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/9/1
 **/
@EnableRabbit
@Configuration
@Slf4j
public class RabbitMQConfig {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
    * 设置rabbitmq发送的消息使用json形式
    */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     *  消息确认机制 -- 发送端确认
     *
     *      定制 RabbitTemplate
     *          1、消息从生产者抵达rabbitmq服务器就回调ConfirmCallback， 集群模式需要到达所以rabbitmq服务器才会回调
     *              1）、spring.rabbitmq.publisher-confirm-type=correlated 或者 spring.rabbitmq.publisher-confirms=true
     *              2）、确认消息回调  rabbitTemplate.setConfirmCallback
     *
     *          2、消息错误从交换机抵达队列进行回调
     *              1）、 spring.rabbitmq.publisher-returns=true
     *                  spring.rabbitmq.template.mandatory=true
     *              2)、 失败消息回调  rabbitTemplate.setReturnCallback
     *
     *  消息确认机制 -- 消费端确认(保证每个消息被正确消费，此时才可以从broker删除这个消息)
     *
     *  spring.rabbitmq.listener.simple.acknowledge-mode=manual 需要首先开启消费端手动确认
     *
     *      1）、默认自动确认，只要消息接收到，客户端会自动确认，服务端就会移除这个消息
     *      可能出现的问题：   只要接到消息就会确认，消息出现错误就会丢失消息， 有可能消息没有完全消费
     *
     *      解决办法： 手动确认
     *
     *      只要我们没有明确告诉MQ，消息被确认消费(即没有ACK)，消息就一直是unacked状态。
     *      即使消费者宕机，消息不会丢失，会重新变成ready模式，等下次新的消费者连接了，消息会重新投递。
     *
     *      2）、签收消息(确认被正确消费)
     *      签收 第二个参数是否全部签收
     *      @see com.rabbitmq.client.Channel#basicAck(long, boolean)
     *      拒签  第一个boolean 是否拒签所有的  第二个boolean 被拒绝的消息是否重新入队
     *      @see com.rabbitmq.client.Channel#basicNack(long, boolean, boolean)
     *      拒签 第二个参数是否重新入队
     *      @see com.rabbitmq.client.Channel#basicReject(long, boolean)
     *
     */
    @PostConstruct  // RabbitMQConfig对象创建完成之后，执行这个方法
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 1、只要消息抵达服务器，才会回调这个方法 ack就=true
             *
             * @param correlationData  当前消息的唯一关联数据（这个是消息的唯一ID）
             * @param ack   消息是否成功收到   true为已收到
             * @param cause  失败了的时候的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("correlationData: {}, ack: {}, cause: {}", correlationData, ack, cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 设置消息的抵达队列的确认回调，但是是在消息没有抵达指定的队列，才会触发这个失败回调
             *
             * @param message          投递失败的消息的详细信息
             * @param replyCode        回复的状态码
             * @param replyText        回复的文本内容
             * @param exchange         当时这个消息是发给哪个交换机
             * @param routingKey       当时这个消息用的哪个路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("message: {}, replyCode: {}, replyText: {}, exchange: {}, routingKey: {}", message, replyCode, replyText, exchange, routingKey);
            }
        });
    }

}
