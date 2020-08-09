package com.sherlock.gmall.product.web;

import com.sherlock.gmall.product.service.SkuInfoService;
import com.sherlock.gmall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @auther Sherlock
 * @date 2020/8/8 20:23
 * @Description:
 */
@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 展示当前Sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem (@PathVariable("skuId") Long skuId, Model model) {

        System.out.println("skuId =" + skuId);

        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        model.addAttribute("skuInfo", skuItemVo);

        return "item";
    }

}
