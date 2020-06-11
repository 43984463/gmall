package com.sherlock.gmall.ware;

import com.sherlock.common.constants.GmallConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @auther Sherlock
 * @date 2020/5/8 21:52
 * @Description:
 */
@EnableFeignClients(basePackages = GmallConstant.GMALL_WARE_BASEPATH + GmallConstant.FEIGN)
@EnableDiscoveryClient
@SpringBootApplication
public class GmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallWareApplication.class, args);
    }

}

