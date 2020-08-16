package com.sherlock.common.exception;

import org.apache.http.HttpStatus;

public interface GmallHttpStatus extends HttpStatus {

    /** {@code 10000 Unknow Exception}*/
    public static final int UNKNOW_EXCEPTION = 10000;

    /** {@code 10001 Valid fail Exception}*/
    public static final int VALID_EXCEPTION = 10001;
    public static final int SMS_CODE_EXCEPTION = 10002;

    /** {@code 11000 Valid fail Exception}*/
    public static final int PRODUCT_UP_EXCEPTION = 11000;

    /** {@code 0 ok}*/
    public static final int RESPONSE_OK = 0;

    public static final int USER_EXIST_EXCEPTION = 15001;
    public static final int PHONE_EXIST_EXCEPTION = 15002;
}
