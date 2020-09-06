package com.sherlock.gmall.order;

/**
 * @auther Sherlock
 * @date 2020/5/8 21:49
 * @Description: gmall-order
 * <p>
 * 使用rabbitmq
 * 1、引入amqp场景(导入依赖)
 *      <dependency>
 *           <groupId>org.springframework.boot</groupId>
 *           <artifactId>spring-boot-starter-amqp</artifactId>
 *      </dependency>
 *
 * @see org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration 就会自动生效， 给容器中自动配置了
 * @see org.springframework.amqp.rabbit.core.RabbitTemplate
 * @see org.springframework.amqp.core.AmqpAdmin
 * @see org.springframework.amqp.rabbit.connection.CachingConnectionFactory
 * @see org.springframework.amqp.rabbit.core.RabbitMessagingTemplate
 *
 * 2、@EnableRabbit
 * 3、{@link org.springframework.boot.autoconfigure.amqp.RabbitProperties} 有所有关于 rabbitmq 的配置信息
 *
 *
 *  AmqpAdmin 组件可以用来创建交换机，队列，绑定关系等
 *  RabbitTemplate 可以用来发送消息
 *  @RabbitListener 可以用来监听接收消息
 *
 *
 *  本地事务失效问题：
 *  同一个对象内事务方法互调默认失效，原因 绕过了代理对象，事务使用代理对象来控制的
 *
 *  解决： 使用代理对象来调用事务方法
 *  1）、引入
 *          <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-aop</artifactId>
 *         </dependency>
*       这个包含了 aspectjweaver
 *  2）、@EnableAspectJAutoProxy(exposeProxy = true)  开启aspectj动态代理功能，以后所有的动态代理都是aspectj创建的(即使没有接口也可以创建动态代理)
 *  3）、 用调用对象本类互调
 *  Object o = AopContext.currentProxy(); 再强制转换为本类 然后进行调用
 *
 *
 *
 *
 *  seata控制分布式事务
 *  参考 http://seata.io/zh-cn/docs/user/quickstart.html
 *  1）、没个微服务先必须创建undo_log表，
 *
 *    drop table if exists undo_log;
 *
 *   CREATE TABLE `undo_log` (
 *       `id` bigint(20) NOT NULL AUTO_INCREMENT,
 *       `branch_id` bigint(20) NOT NULL,
 *       `xid` varchar(100) NOT NULL,
 *      `context` varchar(128) NOT NULL,
 *      `rollback_info` longblob NOT NULL,
 *      `log_status` int(11) NOT NULL,
 *      `log_created` datetime NOT NULL,
 *      `log_modified` datetime NOT NULL,
 *      `ext` varchar(100) DEFAULT NULL,
 *       PRIMARY KEY (`id`),
 *      UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
 *   ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
 *
 *   alter table undo_log comment 'seata日志表';
 *
 *   2）、安装事务协调器
 *   从 https://github.com/seata/seata/releases ,下载服务器软件包，将其解压缩。
 *
 *
 */

import com.sherlock.common.constants.GmallConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients(GmallConstant.GMALL_ORDER_BASEPATH + GmallConstant.FEIGN)
@EnableDiscoveryClient
@SpringBootApplication
public class GmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallOrderApplication.class, args);
    }

}
