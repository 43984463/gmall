package com.sherlock.gmall.coupon.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.coupon.dao.CouponHistoryDao;
import com.sherlock.gmall.coupon.entity.CouponHistoryEntity;
import com.sherlock.gmall.coupon.service.CouponHistoryService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service("couponHistoryService")
public class CouponHistoryServiceImpl extends ServiceImpl<CouponHistoryDao, CouponHistoryEntity> implements CouponHistoryService {

    @Autowired
    private CouponHistoryDao couponHistoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CouponHistoryEntity> page = this.page(
                new Query<CouponHistoryEntity>().getPage(params),
                new QueryWrapper<CouponHistoryEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCouponHistoryRequired(){
        CouponHistoryEntity couponHistoryEntity = new CouponHistoryEntity();
        couponHistoryEntity.setMemberNickName("saveCouponHistoryRequired");
        couponHistoryDao.insert(couponHistoryEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCouponHistoryRequiredException(){

            CouponHistoryEntity couponHistoryEntity = new CouponHistoryEntity();
            couponHistoryEntity.setMemberNickName("saveCouponHistoryRequiredException");
            couponHistoryDao.insert(couponHistoryEntity);
            int i = 10 / 0;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCouponHistoryRequiresNew(){
        CouponHistoryEntity couponHistoryEntity = new CouponHistoryEntity();
        couponHistoryEntity.setMemberNickName("saveCouponHistoryRequiresNew");
        couponHistoryDao.insert(couponHistoryEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCouponHistoryRequiresNewException(){
        CouponHistoryEntity couponHistoryEntity = new CouponHistoryEntity();
        couponHistoryEntity.setMemberNickName("saveCouponHistoryRequiresNewException");
        couponHistoryDao.insert(couponHistoryEntity);
        int i = 10 / 0;
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveCouponHistoryNested(){
        CouponHistoryEntity couponHistoryEntity = new CouponHistoryEntity();
        couponHistoryEntity.setMemberNickName("saveCouponHistoryNested");
        couponHistoryDao.insert(couponHistoryEntity);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveCouponHistoryNestedException(){
        CouponHistoryEntity couponHistoryEntity = new CouponHistoryEntity();
        couponHistoryEntity.setMemberNickName("saveCouponHistoryNestedException");
        couponHistoryDao.insert(couponHistoryEntity);
        int i = 10 / 0;
    }
}