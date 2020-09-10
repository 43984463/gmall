package com.sherlock.gmall.order.web;

import com.alipay.api.AlipayApiException;
import com.sherlock.gmall.order.config.AlipayTemplate;
import com.sherlock.gmall.order.service.OrderService;
import com.sherlock.gmall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @auther Sherlock
 * @date 2020/9/10 22:49
 * @Description:
 */
@Controller
@Slf4j
public class PayWebController {

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        PayVo payVo = orderService.getOrderPayInfo(orderSn);

        /*PayVo payVo = new PayVo();
        payVo.setOut_trade_no(); // 订单号
        payVo.setSubject(); // 订单主题
        payVo.setBody(); // 订单备注
        payVo.setTotal_amount();*/
        String pay = alipayTemplate.pay(payVo);
        log.info("pay: {}", pay);
        return pay;
    }

}
