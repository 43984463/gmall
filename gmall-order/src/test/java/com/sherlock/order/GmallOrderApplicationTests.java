package com.sherlock.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

    /**
     * 1、创建交换机(Exchanges),队列(Queue),绑定关系(Binding)
     *   1）、使用AmqpAdmin创建
     *
     * 2、如何收发消息
     */

    @Test
    public void contextLoads(){

    }

}
