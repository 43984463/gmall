package com.sherlock.gmall.order.controller;

import com.sherlock.gmall.order.entity.OrderReturnReasonEntity;
import com.sherlock.gmall.order.entity.RefundInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @auther Sherlock
 * @date 2020/9/1 20:35
 * @Description:
 */
@RestController
@Slf4j
public class RabbitController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num", defaultValue = "10", required = false) Integer num) {

        for (int i = 0; i < num; i++) {
            if (i % 2 == 0) {
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setName("哈哈");
                reasonEntity.setCreateTime(new Date());

                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity, new CorrelationData(UUID.randomUUID().toString()));
                log.info("消息发送完成：OrderReturnReasonEntity {}", reasonEntity);
            } else {
                RefundInfoEntity refundInfoEntity = new RefundInfoEntity();
                refundInfoEntity.setId(2L);
                refundInfoEntity.setRefundSn("测试");

                // "hello22.java", routingKey设置错误是为了测试交换机到队列的错误回调
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello22.java", refundInfoEntity, new CorrelationData(UUID.randomUUID().toString()));
                log.info("消息发送完成：refundInfoEntity {}", refundInfoEntity);
            }
        }

        return "ok";
    }

}
