package com.sherlock.gmall.ware.config;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.constants.GmallWareConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther Sherlock
 * @date 2020/9/7 23:23
 * @Description: 创建队列
 * <p>
 * 容器中的组件会自动在MQ中自动创建(RabbitMQ中没有的情况下)
 * <p>
 * MQ中如果有就不会再重新创建
 */
@Configuration
@Slf4j
public class WareMQConfig {


    /**
     * 死信（延时）队列
     * <p>
     * Queue(String name,  队列名字
     * boolean durable,  是否持久化
     * boolean exclusive,  是否排他
     * boolean autoDelete, 是否自动删除
     * Map<String, Object> arguments) 属性
     * <p>
     * String name, boolean durable, boolean exclusive, boolean autoDelete,@Nullable Map<String, Object> arguments
     *
     * @return
     */
    @Bean
    public Queue stockDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(GmallConstant.X_DEAD_LETTER_EXCHANGE, GmallWareConstant.STOCK_EVENT_EXCHANGE_NAME); // 死信路由
        arguments.put(GmallConstant.X_DEAD_LETTER_ROUTING_KEY, "stock.release"); // 死信路由键
        arguments.put(GmallConstant.X_MESSAGE_TTL, GmallWareConstant.X_MESSAGE_TTL_TIME); // 消息过期时间 2分钟
        return new Queue(GmallWareConstant.STOCK_LOCKED_QUEUE_NAME, true, false, false, arguments);
    }

    /**
     * 普通队列
     *
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue(GmallWareConstant.STOCK_RELEASE_QUEUE_NAME, true, false, false);
    }


    /**
     * Topic类型的交换机
     *
     * @return
     */
    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange(GmallWareConstant.STOCK_EVENT_EXCHANGE_NAME, true, false);
    }

    /**
     * 绑定死信（延时）队列
     *
     * @return
     */
    @Bean
    public Binding stockLockedBinding() {
        /*
         * String destination, 目的地（队列名或者交换机名字）
         * DestinationType destinationType, 目的地类型（Queue、Exhcange）
         * String exchange,
         * String routingKey,
         * Map<String, Object> arguments
         * */
        return new Binding(GmallWareConstant.STOCK_LOCKED_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                GmallWareConstant.STOCK_EVENT_EXCHANGE_NAME,
                "stock.locked.#",
                null);
    }

    /**
     * 绑定普通队列
     *
     * @return
     */
    @Bean
    public Binding stockReleaseBinding() {
        return new Binding(GmallWareConstant.STOCK_RELEASE_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                GmallWareConstant.STOCK_EVENT_EXCHANGE_NAME,
                "stock.release.#",
                null);
    }

    /*@RabbitListener(queues = GmallWareConstant.STOCK_RELEASE_QUEUE_NAME)
    public void stockHandle(Message message){
        log.info("message: {}", message);
    }*/
}
