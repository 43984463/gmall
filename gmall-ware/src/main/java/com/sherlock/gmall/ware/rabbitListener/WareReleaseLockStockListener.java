package com.sherlock.gmall.ware.rabbitListener;

import com.rabbitmq.client.Channel;
import com.sherlock.common.constants.GmallWareConstant;
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
 */
@Component
@Slf4j
@RabbitListener(queues = GmallWareConstant.STOCK_RELEASE_QUEUE_NAME)
public class WareReleaseLockStockListener {

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
    public void handStockLockedRelease(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {


        try {
            wareSkuService.orderUnLockStock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
