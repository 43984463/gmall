package com.sherlock.gmall.thirdparty.component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther Sherlock
 * @date 2020/8/12 21:47
 * @Description:
 */
@Component
@Slf4j
@Data
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
public class SmsComponent {

    private String host;
    private String path;
    private String appcode;
    private String sign;
    private String skin;

    public Map<String, Object> sendSmsCode (String phone, String code) {
        /*String host = "http://smsmsgs.market.alicloudapi.com";  // 【1】请求地址 支持http 和 https 及 WEBSOCKET
        String path = "/smsmsgs"; // 【2】后缀
        String appcode = "93b7e19861a24c519a7548b17dc16d75"; // 【3】开通服务后 买家中心-查看AppCode  // 老师的appcode
        String param = "lock";  // 【4】请求参数，详见文档描述   发送的内容
        String phone = "13772159968";  //  【4】请求参数，详见文档描述  发送的电话号码
        String sign = "175622";   //  【4】请求参数，详见文档描述   内容模板编号
        String skin = "1";  //  【4】请求参数，详见文档描述         公司自定义开头编号*/
        String urlSend = host + path + "?param=" + code + "&phone=" + phone + "&sign=" + sign + "&skin=" + skin;  // 【5】拼接请求链接
        Map<String, Object> returnMap = new HashMap<>();
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE (中间是英文空格)
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
               /* log.info("正常请求计费(其他均不计费)");
                log.info("获取返回的json:");
                log.info(json);*/
                log.info("send ok. phone number is:" + phone + ", code is:" + code);
                returnMap.put("code", 200);
                returnMap.put("msg", json);
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    //log.info("AppCode错误 ");
                    returnMap.put("code", 400);
                    returnMap.put("msg", "AppCode错误");
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    //log.info("请求的 Method、Path 或者环境错误");
                    returnMap.put("code", 400);
                    returnMap.put("msg", "请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    //log.info("参数错误");
                    returnMap.put("code", 400);
                    returnMap.put("msg", "参数错误");
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    //log.info("服务未被授权（或URL和Path不正确）");
                    returnMap.put("code", 403);
                    returnMap.put("msg", "服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    //log.info("套餐包次数用完 ");
                    returnMap.put("code", 403);
                    returnMap.put("msg", "套餐包次数用完");
                } else {
                    /*log.info("参数名错误 或 其他错误");
                    log.info(error);*/
                    returnMap.put("code", 403);
                    returnMap.put("msg", "参数名错误 或 其他错误");
                }
            }

        } catch (MalformedURLException e) {
            //log.info("URL格式错误");
            returnMap.put("code", 403);
            returnMap.put("msg", "URL格式错误");
        } catch (UnknownHostException e) {
            //log.info("URL地址错误");
            returnMap.put("code", 403);
            returnMap.put("msg", "URL地址错误");
        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            // e.printStackTrace();
            //log.info("其他错误");
            returnMap.put("code", 403);
            returnMap.put("msg", "其他错误");
        }
        return returnMap;
    }

    /*
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

}
