package com.sherlock.gmall.cart.interceptor;

import com.sherlock.common.constants.GmallAuthConstant;
import com.sherlock.common.constants.GmallCartConstant;
import com.sherlock.common.vo.MemberRespVo;
import com.sherlock.gmall.cart.config.GmallCartWebConfig;
import com.sherlock.gmall.cart.vo.UserInfoTo;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @auther Sherlock
 * @date 2020/8/28 21:55
 * @Description: 属于springMVC的拦截器 在执行目标方法之前，判断用户的登录状态。并封装传递给controller
 *
 *
 *
*
 *  浏览器有一个cookie; user-key;标识用户身份。一个月之后过期;
 *  如果第一次使用购物车功能，都会给一个临时用户身份
 *  浏览器保存，每次访问都会带上这个cookie。
 *
 *  登录了： session有
 *  没登录： 使用coolie中的 user-key
 *  第一次使用：系统分配一个 user-key
 *
 *
 *  springboot使用拦截器
 *  1、 创建一个 CartInterceptor 实现 {@link HandlerInterceptor}
 *  2、 把拦截器添加到springMVC中
 *  @see GmallCartWebConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 *
 *    registry.addInterceptor(cartInterceptor).addPathPatterns("/**");  添加 cartInterceptor 拦截器并且拦截所有请求("/**)
 *
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTo userInfoTo = new UserInfoTo();

        HttpSession session = request.getSession();
        MemberRespVo member = (MemberRespVo)session.getAttribute(GmallAuthConstant.GMALL_LOGIN_USER);


        if (member != null){
            userInfoTo.setUserId(member.getId());
            // 用户登录了
        }
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (GmallCartConstant.GMALL_TEMP_USER_COOKIE_NAME.equals(cookie.getName())) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTemp(true);
                }
            }
        }
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }

        // 目标方法执行之前, 给每个进来的线程放入用户信息， 可以在controller层直接取到，不需要每次获取
        threadLocal.set(userInfoTo);

        return true;
    }

    /**
     * 从threadLocal中获取UserInfoTo,然后获取用户是否登录过，没登录过就在
     * 请求返回之前如果cookie里面没有uer-key就往里面放1个，最大时间为1个月
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoTo userInfoTo = threadLocal.get();
        if (!userInfoTo.isTemp()) {
            Cookie cookie = new Cookie(GmallCartConstant.GMALL_TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gmall.com");
            cookie.setMaxAge(GmallCartConstant.GMALL_TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
}
