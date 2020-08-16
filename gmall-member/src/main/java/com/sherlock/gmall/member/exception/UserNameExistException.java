package com.sherlock.gmall.member.exception;

/**
 * @auther Sherlock
 * @date 2020/8/16 22:33
 * @Description:
 */
public class UserNameExistException extends RuntimeException{

    public UserNameExistException() {
        super("用户名已存在");
    }
}
