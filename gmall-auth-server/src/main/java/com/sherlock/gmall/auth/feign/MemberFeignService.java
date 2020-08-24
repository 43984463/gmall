package com.sherlock.gmall.auth.feign;

import com.sherlock.common.to.SocialUserVo;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.auth.vo.MemberRespVo;
import com.sherlock.gmall.auth.vo.UserLoginVo;
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
    R<String> regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
    public R<String> login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    public R<MemberRespVo> oauthLogin(@RequestBody SocialUserVo vo);
}
