package com.sherlock.gmall.member.web;

import com.sherlock.common.utils.Constant;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther Sherlock
 * @date 2020/9/10 23:41
 * @Description:
 */
@RestController
public class MemberWebController {

    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("/memberOrderList")
    public String memberOrderListPage(@RequestParam(value = "pageNum", defaultValue = "1") String pageNum, Model model, HttpServletRequest request){
        // 可以从request中获取到支付宝给我们返回回来的数据
        // 验证签名的方式验证是否是支付宝的签名

        Map<String, Object> page = new HashMap<>();
        page.put(Constant.PAGE, pageNum);
        R r = orderFeignService.listWithItem(page);
        model.addAttribute("orders", r);
        return "orderList";
    }

}
