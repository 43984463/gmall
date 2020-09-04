package com.sherlock.gmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.gmall.ware.entity.WareInfoEntity;
import com.sherlock.common.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:18:07
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);
}

