package com.sherlock.gmall.seckill.scheduled;

import com.sherlock.common.constants.GmallSeckillConstant;
import com.sherlock.gmall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @auther Sherlock
 * @date 2020/9/12 11:26
 * @Description: 秒杀商品定时任务
 */
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 每天晚上3点上架秒杀商品
     *
     * 查当天00:00:00 - 23:59:59的商品
     * 查明天00:00:00 - 23:59:59的商品
     * 查后天00:00:00 - 23:59:59的商品
     *
     * 需要保持秒杀的幂等性
     *
     */
    //@Scheduled(cron = "0 0 3 * * ?")
    @Scheduled(cron = "0/30 * * * * ?")
    public void uploadSeckillSkuLatest3Days(){
        // 使用分布式锁保持幂等性
        RLock lock = redissonClient.getLock(GmallSeckillConstant.SECKILL_UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            long start = System.currentTimeMillis();

            log.info("每天晚上3点上架秒杀商品: 开始: {}", LocalDateTime.now());
            // 已经上架的无需处理
            seckillService.uploadSeckillSkuLatest3Days();

            log.info("每天晚上3点上架秒杀商品: 结束: {}", LocalDateTime.now());
            long end = System.currentTimeMillis();
            log.info("每天晚上3点上架秒杀商品: 耗时: {}", end - start);
        }finally {
            lock.unlock();
        }
    }


}
