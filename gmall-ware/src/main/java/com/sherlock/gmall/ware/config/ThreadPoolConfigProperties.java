package com.sherlock.gmall.ware.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @auther Sherlock
 * @date 2020/8/10 22:19
 * @Description:
 */
@ConfigurationProperties(prefix = "gmall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {

    private Integer corePoolSize = 10;

    private Integer maximumPoolSize = 200;

    private Integer keepAliveTime = 20;

}
