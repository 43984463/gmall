package com.sherlock.gmall.cart.controller;

import com.sherlock.gmall.cart.service.CartService;
import com.sherlock.gmall.cart.vo.Cart;
import com.sherlock.gmall.cart.vo.CartItem;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
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

    @GetMapping({"/cart.html","/cartList",""})
    public String cartListPage(Model model, HttpSession session) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 添加成功之后重定向到  /addToCartSuccessPage.html 请求  重新获取购物车数据 防止一直刷新页面导致一直添加商品
     *
     * redirectAttributes.addAttribute("skuId", skuId);        将数据放在拼接在url后面
     * redirectAttributes.addFlashAttribute("skuId", skuId);   将数据放在session里面可以在页面中取出，但是只能取一次
     *
     * @param skuId
     * @param num
     * @param redirectAttributes
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {

        CartItem cartItem = cartService.addToCart(skuId, num);
        //model.addAttribute("cartItem", cartItem);
        // 放入model中的时候重定向会自动拼接参数
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gmall.com/addToCartSuccessPage.html";
    }

    @GetMapping("/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,  Model model){
        // 重定向到成功页面，再次查询购物车数据即可

        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

    @ApiOperation("改变是否选中")
    @GetMapping("/checkCartItem")
    public String checkCartItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check){
        cartService.checkCartItem(skuId, check);
        return "redirect:http://cart.gmall.com/cart.html";
    }

    @ApiOperation("改变数量")
    @GetMapping("/countCartItem")
    public String countCartItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num){
        cartService.countCartItem(skuId, num);
        return "redirect:http://cart.gmall.com/cart.html";
    }

    @ApiOperation("删除购物项")
    @GetMapping("/deleteCartItem")
    public String deleteCartItem(@RequestParam("skuId") Long skuId){
        cartService.deleteCartItem(skuId);
        return "redirect:http://cart.gmall.com/cart.html";
    }

    @ApiOperation("从登陆的用户获取所有被选中的购物项")
    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems(){
        List<CartItem> userCartItems = cartService.getUserCartItems();
        return userCartItems;
    }

}
