package com.sherlock.gmall.coupon.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.coupon.dao.SeckillSkuRelationDao;
import com.sherlock.gmall.coupon.entity.SeckillSkuRelationEntity;
import com.sherlock.gmall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> wrapper = new QueryWrapper<>();
        String sessionId = params.get("promotionSessionId").toString();
        if (StringUtils.isNotEmpty(sessionId)) {
            wrapper.eq("promotionSessionId", sessionId);
        }

        IPage<SeckillSkuRelationEntity> page = this.page(new Query<SeckillSkuRelationEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

}