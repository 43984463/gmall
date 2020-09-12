package com.sherlock.gmall.seckill.feign;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @auther Sherlock
 * @date 2020/9/12 11:39
 * @Description:
 */
@FeignClient(GmallConstant.GMALL_COUPON)
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/lates3DaySession")
    R getLates3DaySession();

}
