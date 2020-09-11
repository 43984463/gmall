package com.sherlock.common.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/11 20:52
 * @Description:
 */
public abstract class GmallMemberConstant {

    public static final List<String> MEMBER_REQUEST_WHITE_LIST = new ArrayList();

    static {
        // 登录接口
        MEMBER_REQUEST_WHITE_LIST.add("**/member/member/oauth2/login/**");
        // 订单查询邮费接口， 由于是直接从页面发的ajax get请求，未携带session， 暂时先直接过滤掉
        MEMBER_REQUEST_WHITE_LIST.add("**/member/memberreceiveaddress/info/**");
    }

}
