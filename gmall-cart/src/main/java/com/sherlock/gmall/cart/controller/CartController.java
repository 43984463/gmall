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

    /**
     * 添加成功之后重定向到  /addToCartSuccessPage.html 请求  重新获取购物车数据 防止一直刷新页面导致一直添加商品
     * @param skuId
     * @param num
     * @param model
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model) throws ExecutionException, InterruptedException {

        CartItem cartItem = cartService.addToCart(skuId, num);
        //model.addAttribute("cartItem", cartItem);
        // 放入model中的时候重定向会自动拼接参数
        model.addAttribute("skuId", skuId);
        return "/addToCartSuccessPage.html";
    }

    @GetMapping("/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,  Model model){
        // 重定向到成功页面，再次查询购物车数据即可

        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

}
