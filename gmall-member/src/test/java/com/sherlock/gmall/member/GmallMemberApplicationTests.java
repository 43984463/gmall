package com.sherlock.gmall.member;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @auther Sherlock
 * @date 2020/8/16 22:44
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GmallMemberApplicationTests {

    @Test
    public void contextLoads(){
        // MD5加密
        /*String s = DigestUtil.md5Hex("123456");
        System.out.println(s);*/
        // apache的加密
        //Md5Crypt.md5Crypt("123456".getBytes());
        // spring的加密 自动带盐 每次都随机不一样 使用match方法进行判断是否一致
        // $2a$10$mnesICvdwiO/en2mPUto4ePxq7yz8gC3GNxQEhFQ46RIDCi0FdKba
        // $2a$10$Q0KTOurRIG35VHw.sEbHweJtWtkjSaIzAXniNkZ93TMRDPoA.7Cb.
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode + " password is match: " + passwordEncoder.matches("123456", "$2a$10$Q0KTOurRIG35VHw.sEbHweJtWtkjSaIzAXniNkZ93TMRDPoA.7Cb."));
    }

}
