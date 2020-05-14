package com.sherlock.gmall.member.feign;

import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @auther Sherlock
 * @date 2020/5/14 21:20
 * @Description:
 */
@Service
@FeignClient("gmall-coupon")
public interface CouponFeignService {

    @RequestMapping("/coupon/coupon/member/list")
    R couponList();

}
