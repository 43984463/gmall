package com.sherlock.gmall.cart.controller;

import com.sherlock.gmall.cart.service.CartService;
import com.sherlock.gmall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.ExecutionException;

/**
 * @auther Sherlock
 * @date 2020/8/28 20:35
 * @Description:
 */
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping({"/cart.html","/cartList"})
    public String cartListPage(){

        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) throws ExecutionException, InterruptedException {

        CartItem cartItem = cartService.addToCart(skuId, num);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

}
