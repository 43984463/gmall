package com.sherlock.gmall.coupon.service.impl;

import com.sherlock.common.constants.GmallConstant;
import com.sherlock.gmall.coupon.entity.SeckillSkuRelationEntity;
import com.sherlock.gmall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.coupon.dao.SeckillSessionDao;
import com.sherlock.gmall.coupon.entity.SeckillSessionEntity;
import com.sherlock.gmall.coupon.service.SeckillSessionService;
import org.springframework.util.CollectionUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLates3DaySession() {
        List<SeckillSessionEntity> list = list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(), endTime()));
        if (!CollectionUtils.isEmpty(list)) {
            return list.stream().map(seckillSessionEntity -> {
                Long id = seckillSessionEntity.getId();
                List<SeckillSkuRelationEntity> sessions = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                seckillSessionEntity.setRelationSkus(sessions);
                return seckillSessionEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime start = LocalDateTime.of(now, min);
        return start.format(DateTimeFormatter.ofPattern(GmallConstant.DATE_TIME_FORMATTER_STRING));
    }

    private String endTime() {
        LocalDate now = LocalDate.now().plusDays(2);
        LocalTime max = LocalTime.MAX;
        LocalDateTime end = LocalDateTime.of(now, max);
        return end.format(DateTimeFormatter.ofPattern(GmallConstant.DATE_TIME_FORMATTER_STRING));
    }
}