package com.sherlock.common.exception;

import org.apache.http.HttpStatus;

public interface GmallHttpStatus extends HttpStatus {

    /** {@code 10000 Unknow Exception}*/
    public static final int UNKNOW_EXCEPTION = 10000;

    /** {@code 10001 Valid fail Exception}*/
    public static final int VALID_EXCEPTION = 10001;
}
