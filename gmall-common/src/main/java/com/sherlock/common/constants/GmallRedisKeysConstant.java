package com.sherlock.common.constants;

/**
 * @auther Sherlock
 * @date 2020/8/24 21:42
 * @Description:
 */
public interface GmallRedisKeysConstant {

    public static final String GMALL_PRODUCT_REDISKEY_PRIFIX = "product:";
    public static final String GMALL_PRODUCT_REDISKEY_CATELOGJSON = GMALL_PRODUCT_REDISKEY_PRIFIX + "catelogJson";


    public static final String GMALL_AUTH_REDISKEY_PRIFIX = "auth:";
    public static final String GMALL_SMS_CODE_CACHE_PREFIX = GMALL_AUTH_REDISKEY_PRIFIX + "sms:code:";
}
