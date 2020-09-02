package com.sherlock.gmall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @auther Sherlock
 * @date 2020/9/2 23:34
 * @Description:
 */
@Configuration
public class GmallFeignConfig {

    /**
     * Feign在创建的时候会从容器中捞RequestInterceptor类型的过滤器然后加入到自己里面
     * @see feign.Feign.Builder#requestInterceptors
     * @return
     *
     * 1、使用RequestContextHolder获取当前线程的请求上下文
     * 2、从原来的请求中获取过来并放入新的请求中
     */

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                // 老的请求
                HttpServletRequest request = requestAttributes.getRequest();
                /*String cookie = request.getHeader("Cookie");
                // 新的请求中放入原来的Cookie(Cookie中包含登录信息)
                template.header("Cookie", cookie);*/

                // 所有的head都重新放入新的请求
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null){
                    while (headerNames.hasMoreElements()){
                        String headName = headerNames.nextElement();
                        template.header(headName, request.getHeader(headName));
                    }
                }

            }
        };
    }

}
