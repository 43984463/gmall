package com.sherlock.gmall.auth.controller;

import com.sherlock.gmall.auth.config.GmallWebConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @auther Sherlock
 * @date 2020/8/11 22:57
 * @Description:
 */
@Controller
public class LoginController {

    /**
     * 这2个方法的作用是用来跳转页面，没有做任何逻辑处理.
     * @see GmallWebConfig#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry) 可以代替其作用
     * @return
     */

    /*@GetMapping("/login.html")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/reg.html")
    public String regPage() {
        return "reg";
    }*/
}
