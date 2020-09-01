package com.sherlock.gmall.order;

import com.sherlock.gmall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;

/**
 * @auther Sherlock
 * @date 2020/8/31 23:18
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GmallOrderApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 1、创建交换机(Exchanges),队列(Queue),绑定关系(Binding)
     *   1）、使用AmqpAdmin创建
     *
     * 2、如何收发消息
     */

    @Test
    public void createExchange(){
        /**
         * public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
         */
        Exchange directExchange = new DirectExchange("hello-java-exchange", true, false, new HashMap<>());
        amqpAdmin.declareExchange(directExchange);
        log.info("exchange创建成功，名称是：{}", directExchange.getName());
    }

    @Test
    public void createQueue(){
        /**
         * public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete,
         *                        @Nullable Map<String, Object> arguments)
         */
        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("queue创建成功，名称是：{}", queue.getName());
    }

    /**
     * 交换机和队列绑定
     */
    @Test
    public void createBinding(){
        /**
         * public Binding(String destination【目的地】,
         * DestinationType destinationType【目的地类型】,
         * String exchange【交换机】,
         * String routingKey,【路由键】
         * @Nullable Map<String, Object> arguments)
         * 将 exchange 指定的交换机 和 Destination目的地进行绑定， 使用 routingKey 作为路由键
         */
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE, "hello-java-exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("binding创建成功");
    }

    @Test
    public void sendMessageTest(){
        OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
        reasonEntity.setId(1L);
        reasonEntity.setName("哈哈");
        reasonEntity.setCreateTime(new Date());

        // 1、发送消息
        String Message = "Hello World";
        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", Message);
        log.info("消息发送完成：{}", Message);
        // 发送的消息如果是个对象，我们会使用序列化机制，将对象写出去，所以对象必须实现Serializable接口
        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity);
        log.info("消息发送完成：{}", reasonEntity);
    }
}
