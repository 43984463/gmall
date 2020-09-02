package com.sherlock.gmall.order.web;

import com.sherlock.gmall.order.service.OrderService;
import com.sherlock.gmall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @auther Sherlock
 * @date 2020/9/2 20:59
 * @Description:
 */
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单确认页返回的数据
     * @param model
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model){
        OrderConfirmVo confirmVo = orderService.confirmOder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

}
