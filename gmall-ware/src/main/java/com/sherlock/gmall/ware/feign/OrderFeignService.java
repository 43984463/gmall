package com.sherlock.gmall.ware.feign;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @auther Sherlock
 * @date 2020/9/8 23:56
 * @Description:
 */
@FeignClient(GmallConstant.GMALL_ORDER)
public interface OrderFeignService {

    @GetMapping("/order/order/getOrderInfoByOrderSn/{orderSn}")
    R getOrderInfoByOrderSn(@PathVariable("orderSn") String orderSn);

}
