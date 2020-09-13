package com.sherlock.gmall.seckill.config;


import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.sherlock.common.exception.GmallBizCodeEnume;
import com.sherlock.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @auther Sherlock
 * @date 2020/9/13 17:10
 * @Description:
 */
@Configuration
public class SeckillSentinelConfig {

    /**
     * 设置Sentinel出现问题之后的返回   不好用 TODO 后边可以自己试下ExceptionHandler进行统一设置
     */
//    public SeckillSentinelConfig(){
//        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
//            @Override
//            public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException e) throws IOException {
//                R error = R.error(GmallBizCodeEnume.GMALL_TO_NAMY_REQUEST.getCode(), GmallBizCodeEnume.GMALL_TO_NAMY_REQUEST.getMsg());
//                response.setCharacterEncoding("utf-8");
//                response.setContentType("applocation/json");
//                response.getWriter().write(JSON.toJSONString(error));
//            }
//        });
//    }

}
