package com.sherlock.gmall.ware.rabbitListener;

import com.rabbitmq.client.Channel;
import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.constants.GmallWareConstant;
import com.sherlock.common.to.mq.OrderTo;
import com.sherlock.common.to.mq.StockLockedTo;
import com.sherlock.gmall.ware.service.WareSkuService;
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
 *
 *  1）、订单关闭主动解锁库存
 *  2）、库存自己解锁
 *
 *
 */
@Component
@Slf4j
@RabbitListener(queues = GmallWareConstant.STOCK_RELEASE_QUEUE_NAME)
public class WareReleaseLockStockListener {

    private final String className = this.getClass().getName();

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 锁定解锁2种情况：
     * 1）、下订单成功，订单过期没有支付被系统自动取消、或者被用户手动取消。都需要解锁库存
     * 2）、下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前的库存需要解锁
     *
     * @param stockLockedTo
     * @param message
     * @param channel
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {

        // 当前消息是否被第二次及以后(重新)派发过来的
        Boolean redelivered = message.getMessageProperties().getRedelivered();
        log.info("库存自动解锁库存");
        log.info("{}, hand Stock Locked Release, {}",className, stockLockedTo);
        log.info("{} CorrelationId, {}", className, message.getMessageProperties().getHeaders().get(GmallConstant.SPRING_RETURNED_MESSAGE_CORRELATION));
        try {
            wareSkuService.orderUnLockStock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存解锁优先到期，查询订单状态一直是新建状态，直接不做什么就丢弃消息了
     * @param orderTo
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        log.info("订单主动解锁库存");
        log.info("{}, hand Stock Locked Release, {}", className, orderTo);
        log.info("{} CorrelationId, {}", className, message.getMessageProperties().getHeaders().get(GmallConstant.SPRING_RETURNED_MESSAGE_CORRELATION));
        try {
            wareSkuService.orderUnLockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
