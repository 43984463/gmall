package com.sherlock.gmall.order.config;

import com.sherlock.common.constants.GmallConstant;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/6/3
 **/
@Configuration
@EnableSwagger2
@Data
@ConfigurationProperties(prefix = "swagger")
public class Swagger2Config {

    private Boolean enabled = false;

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //加了ApiOperation注解的类，才生成接口文档
                //.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //包下的类，才生成接口文档  好像只能选择一个满足的条件
                .apis(RequestHandlerSelectors.basePackage(GmallConstant.GMALL_CART_BASEPATH + GmallConstant.CONTROLLER))
                .paths(PathSelectors.any())
                .build()
                .enable(enabled);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("gmall谷粒商城后台系统")
                .description("gmall谷粒商城后台gmall-cart模块")
                .version("1.0")
                .build();
    }


}
