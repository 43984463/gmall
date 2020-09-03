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
     *
     * 异步线程下ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();获取到空的情况
     *    原因：
     *      @see com.sherlock.gmall.order.service.impl.OrderServiceImpl#confirmOder() 方法在同步调用时，
     *      ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes() 代码获取的是同一个线程的数据，
     *      @see org.springframework.web.context.request.RequestContextHolder#getRequestAttributes() 是从 ThreadLocal 中获取数据，但是在异步情况下，运行的不是同一个线程，
     *      所以导致RequestContextHolder.getRequestAttributes()方法获取到null (就是开的另外的线程不能从之前线程获取到信息，所以报空指针)
     *
     *   解决办法：
     *     在开多线程的时候往线程里面放入主线程获取到的请求信息
     *     @see com.sherlock.gmall.order.service.impl.OrderServiceImpl#confirmOder()
     *
     */

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                // 防止异步任务产生的获取到null的情况
                if (requestAttributes != null) {
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

            }
        };
    }

}
