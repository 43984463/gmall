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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        if (StringUtils.isNotBlank(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            // System.currentTimeMillis() - l 获得的是毫秒  <60000是60S之内只能发送1次
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        String code = IdUtil.simpleUUID().substring(0, 6);
        String redisValue = code + "_" + System.currentTimeMillis();

        // 缓存验证码
        redisTemplate.opsForValue().set(GmallAuthConstant.SMS_CODE_CACHE_PREFIX + phone, redisValue, 10, TimeUnit.MINUTES);
        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }


    /**
     * 注册成功回到首页，回到登录页
     * <p>
     * return "redirect:http//auth.gmall.com/login.html";
     * <p>
     *
     *
     *  重定向携带数据，是利用session原理。将数据放在session中。只要跳到下一个页面取出这个数据以后，session里面的数据就会被删除。
     *  但是分布式下session会有问题
     *
     *
     * 由于在
     * @see com.sherlock.gmall.auth.config.GmallWebConfig#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
     * 配置了映射，则可以直接跳转到
     * return "redirect:/login.html";
     * return "forward:/reg.html";
     *
     * @param vo      请求参数
     * @param result  返回的错误放在这里面
     * @param model   返回给页面的视图，往这个里面存需要给页面返回的数据
     * @param redirectAttributes   模拟重定向携带数据
     */
    @RequestMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        System.out.println(vo);

        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            Map<String,String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            //model.addAttribute("errors", errors);
            /**
             * 校验出错转发到注册页
             *
             *   出现错误 Request method 'POST' not supported
             *   用户注册 发送 /regist[Post]请求
             *         ---> 验证出错
             *            ---> 转发到 /reg.html(路径映射默认都是Get方式才能访问。) 所以出现上述错误
              */
            // return "forward:/reg.html";


            /**
             * 刷新页面导致重复提交
             */
            // return "reg";

            /**
             * 放在请求域的model丢失
             * 解决办法：
             * 一、方法签名添加  @param redirectAttributes   模拟重定向携带数据
             * 二、使用  redirectAttributes.addFlashAttribute("errors", errors); 把携带的数据放在返回的数据里
             */
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gmall.com/reg.html";
        }

        return "redirect:/login.html";
    }
}
