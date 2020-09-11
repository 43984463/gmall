package com.sherlock.gmall.member.feign;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/9/11
 **/
@FeignClient(GmallConstant.GMALL_ORDER)
public interface OrderFeignService {

    @PostMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);

}
