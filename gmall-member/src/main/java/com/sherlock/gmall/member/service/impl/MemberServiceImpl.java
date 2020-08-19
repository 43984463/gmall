package com.sherlock.gmall.member.service.impl;

import com.sherlock.gmall.member.entity.MemberLevelEntity;
import com.sherlock.gmall.member.exception.PhoneExistException;
import com.sherlock.gmall.member.exception.UserNameExistException;
import com.sherlock.gmall.member.service.MemberLevelService;
import com.sherlock.gmall.member.vo.MemberLoginVo;
import com.sherlock.gmall.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.member.dao.MemberDao;
import com.sherlock.gmall.member.entity.MemberEntity;
import com.sherlock.gmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();

        // 设置会员默认等级
        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());
        // 检查用户名和手机号是否唯一，
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());
        // 设置用户名和手机号
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());

        // 密码进行盐值加密
        // spring的MD5盐值加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberEntity.setPassword(passwordEncoder.encode(vo.getPassword()));

        // 是否启用
        // memberEntity.setStatus(1);
        // TODO 其他默认信息

        save(memberEntity);
    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistException{
        int usernameCount = count(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (usernameCount > 0) {
            throw new UserNameExistException();
        }
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException{
        int phoneCount = count(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (phoneCount > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        MemberEntity memberEntity = getOne(new QueryWrapper<MemberEntity>().eq("username", vo.getLoginAccount()).or().eq("mobile", vo.getLoginAccount()));
        if (memberEntity != null) {
            // 密码对比
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(vo.getPassword(), memberEntity.getPassword());
            if (matches){
                return memberEntity;
            }
        }

        return null;
    }

}