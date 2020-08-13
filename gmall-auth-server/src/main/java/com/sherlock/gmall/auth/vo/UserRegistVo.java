package com.sherlock.gmall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/8/13
 **/
@Data
public class UserRegistVo {

    @NotEmpty(message = "请输入用户名")
    @Length(min = 6, max = 18, message = "用户名必须是6-18位字符")
    private String userName;

    @NotEmpty(message = "请输入密码")
    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    private String password;

    @NotEmpty(message = "请输入手机号")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$/", message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "请输入验证码")
    private String code;

}
