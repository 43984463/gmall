package com.sherlock.gmall.member.dao;

import com.sherlock.gmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:04:30
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
