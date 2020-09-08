package com.sherlock.gmall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sherlock.common.constants.GmallAuthConstant;
import com.sherlock.common.exception.GmallHttpStatus;
import com.sherlock.common.to.SocialUserVo;
import com.sherlock.common.utils.HttpUtils;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.auth.feign.MemberFeignService;
import com.sherlock.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther Sherlock
 * @date 2020/8/21 0:35
 * @Description:
 */
@Controller
@Slf4j
public class OAuth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;


    @Value("${oauth2.weibo.client_id}")
    private String clientId;

    @Value("${oauth2.weibo.client_secret}")
    private String clientSecret;

    @Value("${oauth2.weibo.grant_type}")
    private String grantType;

    @Value("${oauth2.weibo.redirect_uri}")
    private String redirectUri;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession httpSession) throws Exception {
        // 1、根据code换取accessToken;
        Map<String, String> header = new HashMap<>();
        Map<String, String> query = new HashMap<>();


        Map<String, String> map = new HashMap<>();
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("grant_type", grantType);
        map.put("redirect_uri", redirectUri);
        map.put("code", code);

        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", header, query, map);

        // 2、处理响应数据
        if (response.getStatusLine().getStatusCode() == 200) {
            // 获取到了accessToken
            String json = EntityUtils.toString(response.getEntity()); // 将响应的内容转换成json字符串
            SocialUserVo socialUserVo = JSON.parseObject(json, SocialUserVo.class); // 将获取到的json转换成SocialUserVo对象

            // 可以知道是哪个社交用户
            // 当前用户如果是第一次进网站，自动注册进来(为当前用户生成一个会员信息账号，以后这个社交账号就对应指定的会员)
            // 登录或者注册

            R<MemberRespVo> r = memberFeignService.oauthLogin(socialUserVo);

            if (r.getCode() == GmallHttpStatus.RESPONSE_OK) {
                MemberRespVo respVo = r.getData(new TypeReference<MemberRespVo>(){});
                log.info("登录成功：用户信息: {}", respVo.toString());
                httpSession.setAttribute(GmallAuthConstant.GMALL_LOGIN_USER, respVo);
                // 2、登录成功就跳回首页
                return "redirect:http://gmall.com";
            } else {
                return "redirect:http://auth.gmall.com/login.html";
            }
        }else {
            return "redirect:http://auth.gmall.com/login.html";
        }

        // TODO 需要测试确保是否可行
        /*RestTemplate restTemplate = new RestTemplate();
        SocialUserVo socialUserVo = restTemplate.postForObject("https://api.weibo.com/oauth2/access_token", toJSON(map), SocialUserVo.class);
        System.out.println(socialUserVo);*/


    }

}
