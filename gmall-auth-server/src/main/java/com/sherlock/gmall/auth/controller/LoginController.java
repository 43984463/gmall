package com.sherlock.gmall.auth.controller;

import cn.hutool.core.util.IdUtil;
import com.sherlock.common.constants.GmallAuthConstant;
import com.sherlock.common.exception.BizCodeEnume;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.auth.config.GmallWebConfig;
import com.sherlock.gmall.auth.feign.ThirdPartyFeignService;
import com.sherlock.gmall.auth.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @auther Sherlock
 * @date 2020/8/11 22:57
 * @Description:
 */
@Controller
public class LoginController {

    @Resource
    private ThirdPartyFeignService thirdPartyFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 这2个方法的作用是用来跳转页面，没有做任何逻辑处理.
     *
     * @return
     * @see GmallWebConfig#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry) 可以代替其作用
     */

    /*@GetMapping("/login.html")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/reg.html")
    public String regPage() {
        return "reg";
    }*/

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        /**
         * 1、接口防刷
         * 2、验证码的再次校验 存key-phoneNum value-code
         * sms:code:phoneNum -> code
         */
        String redisCode = redisTemplate.opsForValue().get(GmallAuthConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            // System.currentTimeMillis() - l 获得的是毫秒  <60000是60S之内只能发送1次
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        String code = IdUtil.simpleUUID().substring(0, 6)+"_"+System.currentTimeMillis();

        // 缓存验证码
        redisTemplate.opsForValue().set(GmallAuthConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }


    /**
     * 注册成功回到首页，回到登录页
     *
     * return "redirect:http//auth.gmall.com/login.html";
     *
     * 由于在
     * @see com.sherlock.gmall.auth.config.GmallWebConfig#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
     *配置了映射，则可以直接跳转到
     *return "redirect:/login.html";
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result) {

        if (result.hasErrors()) {
            // 校验出错转发到注册页
            return "forward:/reg.html";
        }

        return "redirect:/login.html";
    }
}
