package com.sherlock.gmall.auth.controller;

import com.sherlock.common.utils.R;
import com.sherlock.gmall.auth.config.GmallWebConfig;
import com.sherlock.gmall.auth.feign.ThirdPartyFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * @auther Sherlock
 * @date 2020/8/11 22:57
 * @Description:
 */
@Controller
public class LoginController {

    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;


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

    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {
        String code = UUID.randomUUID().toString().substring(0, 5);
        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }
}
