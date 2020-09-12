package com.sherlock.gmall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @auther Sherlock
 * @date 2020/9/12 10:54
 * @Description:
 *
 *
 *  springboot整合定时任务
 *       @EnableScheduling 开启定时任务
 *      @Scheduled(cron = "* * * * * * ?") 设置定时的时间
 *   spring中的周是几就是几
 *      @see HelloSchedule#hello2()
 *
 *  当前的定时任务阻塞，就会影响下一次的执行
 *   1）、以异步任务的方式提交任务(使用自己的线程池)
 *   2）、设置定时任务的线程池 (有的spring版本不好使)
 *      @see  org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration
 *      @see  org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;  定时任务配置类
 *
 *   3）、@EnableAsync开启异步任务
 *          @Async在需要异步执行的方法上添加
 *      @see org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;  异步任务自动配置类
 *      @see org.springframework.boot.autoconfigure.task.TaskExecutionProperties;    异步任务配置类
 *
 */

@Slf4j
@Component
//@EnableScheduling
//@EnableAsync
// 在config中开启
public class HelloSchedule {


    /**
     * 每月每日每分钟每秒打印一次
     */
    //@Scheduled(cron = "* * * * * ?")
    public void hello1() {
        log.info("hello1 schedule ");
    }

    /**
     * 每月周六每分钟每隔2秒打印一次
     */
    //@Async
    //@Scheduled(cron = "0/2 * * * * 6")
    public void hello2() {
        log.info("Hello2 schedule ");
    }

}
