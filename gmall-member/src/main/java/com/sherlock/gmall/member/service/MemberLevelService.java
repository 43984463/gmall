package com.sherlock.gmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.gmall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:04:29
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MemberLevelEntity getDefaultLevel();
}

