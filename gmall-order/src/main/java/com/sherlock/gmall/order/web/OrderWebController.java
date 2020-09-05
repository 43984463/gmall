package com.sherlock.gmall.order.web;

import com.sherlock.gmall.order.service.OrderService;
import com.sherlock.gmall.order.vo.OrderConfirmVo;
import com.sherlock.gmall.order.vo.OrderSubmitVo;
import com.sherlock.gmall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

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
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

    /**
     * 订单提交,下单功能
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){

        // 下单步骤: 创建订单，验证令牌，验价格，锁库存。。。
        SubmitOrderResponseVo submitOrderResp = orderService.submitOrder(vo);
        if (submitOrderResp.getCode() == 0) {
            // 下单成功去支付页
            model.addAttribute("submitOrderResp", submitOrderResp);
            return "pay";
        } else {
            String msg = "下单失败, ";
            switch (submitOrderResp.getCode()){
                case 1 : msg += "订单信息过期，请刷新再次提交"; break;
                case 2 : msg += "订单商品价格发生变化，请确认后再次提交"; break;
                case 3 : msg += "商品库存不足"; break;
            }
            // 下单失败回到订单确认页重新确认订单
            redirectAttributes.addFlashAttribute("msg", msg);
            return "redirect:http://cart.gmall.com/toTrade";
        }

    }
}
