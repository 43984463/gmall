package com.sherlock.gmall.member.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sherlock.common.exception.GmallHttpStatus;
import com.sherlock.common.to.SocialUserVo;
import com.sherlock.common.utils.HttpUtils;
import com.sherlock.gmall.member.entity.MemberLevelEntity;
import com.sherlock.gmall.member.exception.PhoneExistException;
import com.sherlock.gmall.member.exception.UserNameExistException;
import com.sherlock.gmall.member.service.MemberLevelService;
import com.sherlock.gmall.member.vo.MemberLoginVo;
import com.sherlock.gmall.member.vo.MemberRegistVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.member.dao.MemberDao;
import com.sherlock.gmall.member.entity.MemberEntity;
import com.sherlock.gmall.member.service.MemberService;

@Slf4j
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
    public MemberEntity regist(MemberRegistVo vo) {
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

        memberEntity.setNickname(vo.getUserName());
        // 是否启用
        // memberEntity.setStatus(1);
        // TODO 其他默认信息

        save(memberEntity);
        return memberEntity;
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

    @Override
    public MemberEntity oauth2Login(SocialUserVo socialUserVo) {
        // 社交登录 + 注册合并逻辑
        String uid = socialUserVo.getUid();
        // 1、判断当前社交用户是否已经登录过系统
        MemberEntity memberEntity = getOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if (memberEntity != null) {
            // 该用户已经注册
            MemberEntity entity = new MemberEntity();
            entity.setId(memberEntity.getId());
            entity.setAccessToken(socialUserVo.getAccess_token());
            entity.setExpiresIn(socialUserVo.getExpires_in());

            updateById(entity);

            memberEntity.setAccessToken(socialUserVo.getAccess_token());
            memberEntity.setExpiresIn(socialUserVo.getExpires_in());
            return memberEntity;
        } else {
            // 没有查到对应的记录，需要注册
            MemberEntity regist = new MemberEntity();
            regist.setSocialUid(socialUserVo.getUid());
            regist.setAccessToken(socialUserVo.getAccess_token());
            regist.setExpiresIn(socialUserVo.getExpires_in());

            Map<String,String> query = new HashMap<>();
            query.put("access_token", socialUserVo.getAccess_token());
            query.put("uid", socialUserVo.getUid());
            try {
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                if (response.getStatusLine().getStatusCode() == GmallHttpStatus.SC_OK) {
                    String json = EntityUtils.toString(response.getEntity());
                    log.info("oauth 获得用户信息: {}", json);
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    regist.setNickname(name);
                    regist.setGender("m".equalsIgnoreCase(gender) ? 1 : 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            save(regist);
            return regist;
        }
    }

}