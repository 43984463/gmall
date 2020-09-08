package com.sherlock.gmall.order.feign;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/2 22:56
 * @Description:
 */
@FeignClient(GmallConstant.GMALL_CART)
public interface CartFeignService {

    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();

}
