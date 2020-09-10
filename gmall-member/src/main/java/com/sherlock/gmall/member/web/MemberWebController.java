package com.sherlock.gmall.member.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther Sherlock
 * @date 2020/9/10 23:41
 * @Description:
 */
@RestController
public class MemberWebController {

    @GetMapping("/memberOrderList")
    public String memberOrderListPage(){
        return "orderList";
    }

}
