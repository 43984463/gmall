package com.sherlock.gmall.seckill.config;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.constants.GmallOrderConstant;
import com.sherlock.common.constants.GmallWareConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther Sherlock
 * @date 2020/9/7 23:23
 * @Description: 创建队列
 *
 * 容器中的组件会自动在MQ中自动创建(RabbitMQ中没有的情况下)
 *
 * MQ中如果有就不会再重新创建
 */
@Configuration
public class SeckillMQConfig {

    /**
     *    死信队列
     *
     *    Queue(String name,  队列名字
     *    boolean durable,  是否持久化
     *    boolean exclusive,  是否排他
     *    boolean autoDelete, 是否自动删除
     *    Map<String, Object> arguments) 属性
     *
     * String name, boolean durable, boolean exclusive, boolean autoDelete,@Nullable Map<String, Object> arguments
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(GmallConstant.X_DEAD_LETTER_EXCHANGE, GmallOrderConstant.ORDER_EVENT_EXCHANGE); // 死信路由
        arguments.put(GmallConstant.X_DEAD_LETTER_ROUTING_KEY, GmallOrderConstant.ORDER_RELEASE_ORDER_ROUTING_KEY_NAME); // 死信路由键
        arguments.put(GmallConstant.X_MESSAGE_TTL, GmallOrderConstant.X_MESSAGE_TTL_TIME); // 消息过期时间 1分钟
        return new Queue(GmallOrderConstant.ORDER_DELAY_QUEUE_NAME, true, false, false, arguments);
    }

    /**
     *  普通队列
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue(){
        return new Queue(GmallOrderConstant.ORDER_RELEASE_ORDER_QUEUE_NAME, true, false, false);
    }


    /**
     * Topic类型的交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange(GmallOrderConstant.ORDER_EVENT_EXCHANGE,true,false);
    }

    /**
     * 绑定死信队列
     * @return
     */
    @Bean
    public Binding orderCreateOrderBinding(){
        /*
         * String destination, 目的地（队列名或者交换机名字）
         * DestinationType destinationType, 目的地类型（Queue、Exhcange）
         * String exchange,
         * String routingKey,
         * Map<String, Object> arguments
         * */
        return new Binding(GmallOrderConstant.ORDER_DELAY_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                GmallOrderConstant.ORDER_EVENT_EXCHANGE,
                GmallOrderConstant.ORDER_CREATE_ORDER_ROUTING_KEY_NAME,
                null);
    }

    /**
     * 绑定普通队列
     * @return
     */
    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding(GmallOrderConstant.ORDER_RELEASE_ORDER_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                GmallOrderConstant.ORDER_EVENT_EXCHANGE,
                GmallOrderConstant.ORDER_RELEASE_ORDER_ROUTING_KEY_NAME,
                null);
    }

    /**
     *
     * 订单释放之后  直接释放库存
     * 订单的交换机绑定解锁库存的消息队列
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding(){
        return new Binding(GmallWareConstant.STOCK_RELEASE_QUEUE_NAME,
                Binding.DestinationType.QUEUE,
                GmallOrderConstant.ORDER_EVENT_EXCHANGE,
                GmallOrderConstant.ORDER_RELEASE_OTHER_ROUTING_KEY_NAME,
                null);
    }
}
