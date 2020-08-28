package com.sherlock.gmall.cart.vo;

import lombok.Data;

/**
 * @auther Sherlock
 * @date 2020/8/28 22:08
 * @Description:
 */
@Data
public class UserInfoTo {

    private Long userId;
    private String userKey;
    private boolean temp = false; //cookie里面有"user-key"就是true，没有就是false

}
