package com.sherlock.gmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.gmall.member.entity.MemberEntity;
import com.sherlock.gmall.member.exception.PhoneExistException;
import com.sherlock.gmall.member.exception.UserNameExistException;
import com.sherlock.gmall.member.vo.MemberRegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:04:30
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    void checkUserNameUnique(String userName) throws UserNameExistException;

    void checkPhoneUnique(String phone) throws PhoneExistException;
}

