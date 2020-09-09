package com.sherlock.gmall.order.rabbitListener;

import com.rabbitmq.client.Channel;
import com.sherlock.gmall.order.entity.OrderReturnReasonEntity;
import com.sherlock.gmall.order.entity.RefundInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/9/1
 **/
@Component
@Slf4j
@RabbitListener(queues = {"hello-java-queue"})
public class RabbitReceive {

    /**
     * https://www.jianshu.com/p/911d987b5f11  RabbitMQ讲解
     *
     *
     * 方法中参数中可以写的参数
     * 1、 org.springframework.amqp.core.Message 原生消息详细信息， 头+体
     * 2、 T<发送的消息的类型> spring会自动转换
     * 3、 传输数据的通道
     *
     * Queue: 可以很多人监听。只要收到消息，队列删除消息，而且只能有一个收到此消息
     * 场景：
     *   1）、订单服务启动多个(多个客户端监听)
     *          同一个消息，只会有一个客户端收到
     *   2）、监听代码逻辑长，阻塞
     *          发送是直接发送，处理消息是处理完成只会才能接收并处理接下来的消息
     *
     *  @RabbitListener 标注的类必须放入spring容器中
     *
     *  @RabbitListener 可以标注在类和方法上  监听队列
     *  @RabbitHandler 只能标注在方法上
     *
     *  @RabbitListener 可以标注在类上面，需配合 @RabbitHandler 注解一起使用
     *  @RabbitListener 标注在类上面表示当有收到消息的时候，就交给 @RabbitHandler 的方法处理，具体使用哪个方法处理，根据 MessageConverter 转换后的参数类型
     *
     *
     *
     *  @Payload 与 @Headers
     * 使用 @Payload 和 @Headers 注解可以消息中的 body 与 headers 信息
     *
     * @RabbitListener(queues = "debug")
     * public void processMessage1(@Payload String body, @Headers Map<String,Object> headers) {
     *     System.out.println("body："+body);
     *     System.out.println("Headers："+headers);
     * }
     *
     * 也可以获取单个 Header 属性
     * @RabbitListener(queues = "debug")
     * public void processMessage1(@Payload String body, @Header String token) {
     *     System.out.println("body："+body);
     *     System.out.println("token："+token);
     * }
     *
     * 通过 @RabbitListener 注解声明 Binding
     *
     * @RabbitListener(bindings = @QueueBinding(
     *         exchange = @Exchange(value = "topic.exchange",durable = "true",type = "topic"),
     *         value = @Queue(value = "consumer_queue",durable = "true"),
     *         key = "key.#"
     * ))
     * public void processMessage1(Message message) {
     *     System.out.println(message);
     * }
     *
     * @param message
     */
    @RabbitHandler
    public void receiveMessage(Message message, OrderReturnReasonEntity reasonEntity, Channel channel){
        byte[] body = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        log.info("receiveMessage 接收到消息。。。,内容{}", message);
        log.info("receiveMessage 接收到消息。。。,内容{}", reasonEntity);

        /**
         * channel内按照顺序自增
         * channel.basicAck(long deliveryTag, boolean multiple)方法的第一个参数  第二个参数 true表示批量签收(确认)，false表示单个签收(确认)
         */

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            if (deliveryTag%2 == 0){
                channel.basicAck(deliveryTag, false);
            } else {
                /**
                 * void basicNack(long deliveryTag, boolean multiple, boolean requeue) throws IOException;
                 * boolean multiple 是否拒签所有消息
                 * boolean requeue 是否重新入队  true 重新入队 false 丢弃
                 */

                // channel.basicNack(deliveryTag, false, true);
                /**
                 * void basicReject(long deliveryTag, boolean requeue) throws IOException;
                 * boolean requeue 是否重新入队  true 重新入队 false 丢弃
                 */
                channel.basicReject(deliveryTag, false);
            }
        } catch (IOException e) {
            // 出现网络抖动
            // e.printStackTrace();
        }
    }

    @RabbitHandler
    public void receiveMessage2(RefundInfoEntity refundInfoEntity){
        log.info("receiveMessage2 接收到消息。。。,内容{}",refundInfoEntity);
    }
}
