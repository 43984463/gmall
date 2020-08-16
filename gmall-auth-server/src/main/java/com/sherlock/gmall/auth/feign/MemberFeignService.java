package com.sherlock.gmall.auth.feign;

import com.sherlock.common.utils.R;
import com.sherlock.gmall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @auther Sherlock
 * @date 2020/8/16 23:21
 * @Description:
 */
@FeignClient("gmall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);

}
