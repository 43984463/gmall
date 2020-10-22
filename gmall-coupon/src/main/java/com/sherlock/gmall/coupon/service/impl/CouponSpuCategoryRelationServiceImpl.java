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

import com.sherlock.gmall.coupon.dao.CouponSpuCategoryRelationDao;
import com.sherlock.gmall.coupon.entity.CouponSpuCategoryRelationEntity;
import com.sherlock.gmall.coupon.service.CouponSpuCategoryRelationService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service("couponSpuCategoryRelationService")
public class CouponSpuCategoryRelationServiceImpl extends ServiceImpl<CouponSpuCategoryRelationDao, CouponSpuCategoryRelationEntity> implements CouponSpuCategoryRelationService {

    @Autowired
    private CouponSpuCategoryRelationDao couponSpuCategoryRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CouponSpuCategoryRelationEntity> page = this.page(
                new Query<CouponSpuCategoryRelationEntity>().getPage(params),
                new QueryWrapper<CouponSpuCategoryRelationEntity>()
        );

        return new PageUtils(page);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCouponRequired(){
        CouponSpuCategoryRelationEntity couponEntity = new CouponSpuCategoryRelationEntity();
        couponEntity.setCategoryName("saveCouponRequired");
        couponSpuCategoryRelationDao.insert(couponEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCouponRequiredException(){
        try {

            CouponSpuCategoryRelationEntity couponEntity = new CouponSpuCategoryRelationEntity();
            couponEntity.setCategoryName("saveCouponRequiredException");
            couponSpuCategoryRelationDao.insert(couponEntity);
            int i = 10 / 0;
        }catch (Exception e){

        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCouponRequiresNew(){
        CouponSpuCategoryRelationEntity couponEntity = new CouponSpuCategoryRelationEntity();
        couponEntity.setCategoryName("saveCouponRequiresNew");
        couponSpuCategoryRelationDao.insert(couponEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCouponRequiresNewException(){
        CouponSpuCategoryRelationEntity couponEntity = new CouponSpuCategoryRelationEntity();
        couponEntity.setCategoryName("saveCouponRequiresNewException");
        couponSpuCategoryRelationDao.insert(couponEntity);
        int i = 10 / 0;
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveCouponNested(){
        CouponSpuCategoryRelationEntity couponEntity = new CouponSpuCategoryRelationEntity();
        couponEntity.setCategoryName("saveCouponNested");
        couponSpuCategoryRelationDao.insert(couponEntity);
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveCouponNestedException(){
        CouponSpuCategoryRelationEntity couponEntity = new CouponSpuCategoryRelationEntity();
        couponEntity.setCategoryName("saveCouponNestedException");
        couponSpuCategoryRelationDao.insert(couponEntity);
        int i = 10 / 0;
    }

}