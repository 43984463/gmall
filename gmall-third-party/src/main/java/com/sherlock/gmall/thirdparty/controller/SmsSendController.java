package com.sherlock.gmall.thirdparty.controller;

import com.sherlock.common.utils.R;
import com.sherlock.gmall.thirdparty.component.SmsComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @auther Sherlock
 * @date 2020/8/12 23:50
 * @Description:
 */
@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsSendController {

    @Autowired
    private SmsComponent smsComponent;

    /**
     * 提供给其他服务进行调用
     * @return
     */
    @GetMapping("/sendcode")
    public R sendCode (@RequestParam("phone") String phone, @RequestParam("code") String code) {
        Map<String, Object> smsCode = smsComponent.sendSmsCode(phone, code);
        log.info("code is: {}, response msg is: {}", smsCode.get("code"), smsCode.get("msg"));
        return R.ok().setData(smsCode);
    }

}
