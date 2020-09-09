package com.sherlock.gmall.order.rabbitListener;

import com.rabbitmq.client.Channel;
import com.sherlock.common.constants.GmallOrderConstant;
import com.sherlock.gmall.order.entity.OrderEntity;
import com.sherlock.gmall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @auther Sherlock
 * @date 2020/9/8 23:22
 * @Description:
 */
@Component
@Slf4j
@RabbitListener(queues = GmallOrderConstant.ORDER_RELEASE_ORDER_QUEUE_NAME)
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    /**
     * 订单关闭
     *
     * @param orderEntity
     * @param message
     * @param channel
     */
    @RabbitHandler
    public void handOrderClose(OrderEntity orderEntity, Message message, Channel channel) throws IOException {
        log.info("OrderCloseListener hand Order Close, {}", orderEntity);

        try {
            orderService.closeOrder();
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
