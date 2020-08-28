package com.sherlock.gmall.cart.controller;

import com.sherlock.common.constants.GmallAuthConstant;
import com.sherlock.common.constants.GmallConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @auther Sherlock
 * @date 2020/8/28 20:35
 * @Description:
 */
@Controller
public class CartController {

    @GetMapping({"/cart.html","/cartList"})
    public String cartListPage(){

        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num){
        return "success";
    }

}
