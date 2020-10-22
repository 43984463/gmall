package com.sherlock.gmall.coupon.service.impl;

import com.sherlock.gmall.coupon.service.CouponHistoryService;
import com.sherlock.gmall.coupon.service.CouponService;
import com.sherlock.gmall.coupon.service.TestSpringTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @auther Sherlock
 * @date 2020/9/17 20:31
 * @Description:
 */
@Service("testSpringTransactionalService")
public class TestSpringTransactionalServiceImpl implements TestSpringTransactionalService {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponHistoryService couponHistoryService;

    @Autowired
    private CouponSpuCategoryRelationServiceImpl couponSpuCategoryRelationService;

    @Override
    @Transactional
    public void saveSpringTest1() {
        couponService.saveCouponRequired();
//        try {
            couponSpuCategoryRelationService.saveCouponRequired();
//        } catch (Exception e) {
//
//        }
        couponHistoryService.saveCouponHistoryRequiresNew();
    }
}
