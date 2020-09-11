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
        MEMBER_REQUEST_WHITE_LIST.add("**/member/member/oauth2/login/**");
        MEMBER_REQUEST_WHITE_LIST.add("**/member/memberreceiveaddress/info/**");
    }

}
