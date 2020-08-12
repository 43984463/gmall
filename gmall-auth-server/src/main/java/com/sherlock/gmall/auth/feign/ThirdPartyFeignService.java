package com.sherlock.gmall.auth.feign;

import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @auther Sherlock
 * @date 2020/8/13 0:03
 * @Description:
 */

@FeignClient("gmall-third-party")
public interface ThirdPartyFeignService {

    @GetMapping("/sms/sendcode")
    public R sendCode (@RequestParam("phone") String phone, @RequestParam("code") String code);

}
