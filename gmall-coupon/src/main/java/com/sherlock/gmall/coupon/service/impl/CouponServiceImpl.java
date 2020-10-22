package com.sherlock.gmall.coupon.service.impl;

import com.sherlock.gmall.coupon.entity.CouponEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.coupon.dao.CouponDao;
import com.sherlock.gmall.coupon.entity.CouponEntity;
import com.sherlock.gmall.coupon.service.CouponService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service("couponService")
public class CouponServiceImpl extends ServiceImpl<CouponDao, CouponEntity> implements CouponService {

    @Autowired
    private CouponDao couponDao;
    
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CouponEntity> page = this.page(
                new Query<CouponEntity>().getPage(params),
                new QueryWrapper<CouponEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity saveCouponRequired(){
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("saveCouponRequired");
        couponDao.insert(couponEntity);
        return couponEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCouponRequiredException(){
            CouponEntity couponEntity = new CouponEntity();
            couponEntity.setCouponName("saveCouponRequiredException");
            couponDao.insert(couponEntity);
            int i = 10 / 0;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCouponRequiresNew(){
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("saveCouponRequiresNew");
        couponDao.insert(couponEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCouponRequiresNewException(){
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("saveCouponRequiresNewException");
        couponDao.insert(couponEntity);
        int i = 10 / 0;
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveCouponNested(){
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("saveCouponNested");
        couponDao.insert(couponEntity);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveCouponNestedException(){
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("saveCouponNestedException");
        couponDao.insert(couponEntity);
        int i = 10 / 0;
    }

}