package com.sherlock.gmall.member.exception;

/**
 * @auther Sherlock
 * @date 2020/8/16 22:34
 * @Description:
 */
public class PhoneExistException extends RuntimeException {

    public PhoneExistException() {
        super("手机号已存在");
    }
}
