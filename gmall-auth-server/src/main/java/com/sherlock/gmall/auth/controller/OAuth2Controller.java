package com.sherlock.gmall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.sherlock.common.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.fastjson.JSON.toJSON;

/**
 * @auther Sherlock
 * @date 2020/8/21 0:35
 * @Description:
 */
@Controller
public class OAuth2Controller {

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code) throws Exception {
        // 1、根据code换取accessToken;
        Map<String, String> header = new HashMap<>();
        Map<String, String> query = new HashMap<>();


        Map<String, String> map = new HashMap<>();
        map.put("client_id", "1509412573");
        map.put("client_secret", "c08bbf7a9174e5f218f8125f997ed2bd");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gmall.com/oauth2.0/weibo/success");
        map.put("code", code);

        /*HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", header, query, map);

        // 2、处理响应数据
        if (response.getStatusLine().getStatusCode() == 200) {
            // 获取到了accessToken
            String json = EntityUtils.toString(response.getEntity()); // 将响应的内容转换成json字符串
            SocialUserVo socialUserVo = JSON.parseObject(json, SocialUserVo.class); // 将获取到的json转换成SocialUserVo对象
        }else {
            return "redirect:http://auth.gmall.com/login.html";
        }*/

        RestTemplate restTemplate = new RestTemplate();
        SocialUserVo socialUserVo = restTemplate.postForObject("https://api.weibo.com/oauth2/access_token", toJSON(map), SocialUserVo.class);
        System.out.println(socialUserVo);

        // 2、登录成功就跳回首页
        return "redirect:http://gmall.com";
    }

}
