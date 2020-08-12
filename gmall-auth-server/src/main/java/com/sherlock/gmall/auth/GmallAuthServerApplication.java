package com.sherlock.gmall.auth;

import com.sherlock.common.constants.GmallConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @auther Sherlock
 * @date 2020/8/11 22:18
 * @Description:
 */
@EnableFeignClients(GmallConstant.GMALL_AUTH_BASEPATH + GmallConstant.FEIGN)
@EnableDiscoveryClient
@SpringBootApplication
public class GmallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallAuthServerApplication.class, args);
    }

}