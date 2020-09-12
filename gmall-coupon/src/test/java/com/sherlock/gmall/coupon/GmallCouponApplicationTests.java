package com.sherlock.gmall.coupon;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @auther Sherlock
 * @date 2020/5/9 1:11
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GmallCouponApplicationTests {


    @Test
    public void testLocalDate(){
        LocalDate now = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();
        log.info("now: {}", now);
        log.info("now: {}", dateTime);

        LocalTime min = LocalTime.MIN;
        LocalTime max = LocalTime.MAX;
        log.info("min: {}", min);
        log.info("max: {}", max);

        LocalDateTime dateTime1 = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime dateTime2 = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        log.info("dateTime1: {}", min);
        log.info("dateTime2: {}", max);

        LocalDateTime min1 = LocalDateTime.now().MIN;
        LocalDateTime max1 = LocalDateTime.now().MAX;

        log.info("min1: {}", min1);
        log.info("max1: {}", max1);
    }
}
