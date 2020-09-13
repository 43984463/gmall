package com.sherlock.common.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/9/12 12:52
 * @Description:
 */
public abstract class GmallSeckillConstant {


    public static final List<String> SECKILL_REQUEST_WHITE_LIST = new ArrayList();

    static {
        // 返回所有的当前准备秒杀的商品
        SECKILL_REQUEST_WHITE_LIST.add("**/currentSeckillSkus/**");
        // 查询当前商品是否秒杀的状态信息
        SECKILL_REQUEST_WHITE_LIST.add("**/sku/seckill/**");
    }

    public static final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    public static final String SKUKILL_SESSIONS_CACHE_PREFIX = "seckill:skus";
    public static final String SKU_STOCK_SEMAPHORE_PREFIX = "seckill:stock:"; // +商品随机码
    public static final String SECKILL_UPLOAD_LOCK = "seckill:upload:lock";


    public static final String ORDER_SECKILL_ORDER_QUEUE_NAME = "order.seckill.order.queue";
    public static final String ORDER_SECKILL_ROUTING_KEY_NAME = "order.seckill.order";
}
