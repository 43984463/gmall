package com.sherlock.gmall.product.feign.fallback;

import com.sherlock.common.exception.GmallBizCodeEnume;
import com.sherlock.common.utils.R;
import com.sherlock.gmall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @auther Sherlock
 * @date 2020/9/13 18:58
 * @Description:
 */
@Slf4j
@Component
public class SeckillFeignServiceCallBack implements SeckillFeignService {

    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("SeckillFeignServiceCallBack#getSkuSeckillInfo熔断方法调用");
        return R.error(GmallBizCodeEnume.GMALL_TO_NAMY_REQUEST.getCode(), GmallBizCodeEnume.GMALL_TO_NAMY_REQUEST.getMsg());
    }

}
