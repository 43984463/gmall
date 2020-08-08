package com.sherlock.gmall.product.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @auther Sherlock
 * @date 2020/8/8 20:23
 * @Description:
 */
@Controller
public class ItemController {

    /**
     * 展示当前Sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem (@PathVariable("skuId") Long skuId) {

        System.out.println("skuId =" + skuId);

        return "item";
    }

}
