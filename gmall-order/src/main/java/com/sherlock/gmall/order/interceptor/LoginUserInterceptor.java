package com.sherlock.gmall.order.interceptor;

import com.sherlock.common.constants.GmallAuthConstant;
import com.sherlock.common.vo.MemberRespVo;
import com.sherlock.gmall.order.config.GmallOrderWebConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * @auther Sherlock
 * @date 2020/8/28 21:55
 * @Description: 属于springMVC的拦截器 在执行目标方法之前，判断用户的登录状态。并封装传递给controller
 *
 *  springboot使用拦截器
 *  1、 创建一个 LoginUserInterceptor 实现 {@link HandlerInterceptor}
 *  2、 把拦截器添加到springMVC中
 *  @see GmallOrderWebConfiguration#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 *
 *    registry.addInterceptor(LoginUserInterceptor).addPathPatterns("/**");  添加 cartInterceptor 拦截器并且拦截所有请求("/**)
 *
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /**
         * 解锁库存需要查询订单信息，防止被拦截，需要直接放行
         *
         * @see com.sherlock.gmall.ware.rabbitListener.ReleaseLockStock#handStockLockedRelease(com.sherlock.common.to.mq.StockLockedTo, org.springframework.amqp.core.Message, com.rabbitmq.client.Channel)
         */
        StringBuffer requestURL = request.getRequestURL();
        boolean match = new AntPathMatcher().match("/order/order/stauts/**", requestURL.toString());
        if (match){
            return true;
        }

        HttpSession session = request.getSession();
        MemberRespVo member = (MemberRespVo)session.getAttribute(GmallAuthConstant.GMALL_LOGIN_USER);

        if (request.getRequestURL().toString().endsWith("swagger-ui.html")) {
            return true;
        }

        if (member != null){
            // 用户登录了
            loginUser.set(member);
            return true;
        } else {
            request.getSession().setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.gmall.com");
            return false;
        }

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
   /* @Override
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
    }*/
}
