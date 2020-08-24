package com.sherlock.common.to;

import lombok.Data;
import lombok.ToString;

/**
 * @auther Sherlock
 * @date 2020/8/21 1:08
 * @Description:
 */
@Data
@ToString
public class SocialUserVo {

    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;

}
