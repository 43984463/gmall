package com.sherlock.gmall.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.SessionRepositoryFilter;

/**
 *
 * @auther Sherlock
 * @date 2020/8/25 21:31
 * @Description:
 *
 * 核心原理
 * 1）、@EnableRedisHttpSession导入 @Import(RedisHttpSessionConfiguration.class)配置
 *  1、{@link RedisHttpSessionConfiguration} 加入了 {@link RedisIndexedSessionRepository} 组件， redis操作session。 session的增删改查封装类。
 *  2、{@link RedisHttpSessionConfiguration}  继承 {@link SpringHttpSessionConfiguration}
 *
 *     <li>SessionRepositoryFilter - is responsible for wrapping the HttpServletRequest with
 *      an implementation of HttpSession that is backed by a SessionRepository</li>
 *      添加了1个 SessionRepositoryFilter 过滤器
 *     @see SpringHttpSessionConfiguration#springSessionRepositoryFilter(org.springframework.session.SessionRepository)
 *
 *     请求 -> filter(OncePerRequestFilter implements Filter) {@link org.springframework.session.web.http.OncePerRequestFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
 *     ->  {@link org.springframework.session.web.http.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
 *     ->  {@link SessionRepositoryFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}
 *
 *     doFilterInternal 方法把 HttpServletRequest request, HttpServletResponse response 进行了包装
 *
 *     获取 session
 *     @see SessionRepositoryFilter.SessionRepositoryRequestWrapper#getSession()
 *
 */
@EnableRedisHttpSession
@Configuration
public class SpringSessionConfig {

    /**
     * 提升session的作用域， 父域可以取到子域的的session
     * @return
     */
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // 设置session的作用域(Domain)  扩大作用域
        cookieSerializer.setDomainName("gmall.com");
        // cookieSerializer.setCookieName("GMALLSESSION");
        return cookieSerializer;
    }

    /**
     * 设置redis序列化机制   使用json进行序列化存储
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }
}
