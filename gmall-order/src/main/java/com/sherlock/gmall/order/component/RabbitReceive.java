package com.sherlock.gmall.order.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/9/1
 **/
@Component
@Slf4j
public class RabbitReceive {

    @RabbitListener(queues = {"hello-java-queue"})
    public void receiveMessage(Object message){
        log.info("接收到消息。。。,内容{}", message);
    }

}
