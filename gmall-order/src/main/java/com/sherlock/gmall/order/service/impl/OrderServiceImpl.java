package com.sherlock.gmall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.constants.GmallOrderConstant;
import com.sherlock.common.exception.CheckPriceDiffException;
import com.sherlock.common.exception.GmallHttpStatus;
import com.sherlock.common.exception.NoStockException;
import com.sherlock.common.to.SkuHasStockVo;
import com.sherlock.common.to.mq.OrderTo;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.FareVo;
import com.sherlock.common.vo.MemberAddressVo;
import com.sherlock.common.vo.MemberRespVo;
import com.sherlock.common.vo.OrderItemVo;
import com.sherlock.common.vo.SpuInfoVo;
import com.sherlock.common.vo.WareSkuLockVo;
import com.sherlock.gmall.order.config.GmallFeignConfig;
import com.sherlock.gmall.order.config.OrderMQConfig;
import com.sherlock.gmall.order.dao.OrderDao;
import com.sherlock.gmall.order.entity.OrderEntity;
import com.sherlock.gmall.order.entity.OrderItemEntity;
import com.sherlock.gmall.order.feign.CartFeignService;
import com.sherlock.gmall.order.feign.MemberFeignService;
import com.sherlock.gmall.order.feign.ProductFeignService;
import com.sherlock.gmall.order.feign.WmsFeignService;
import com.sherlock.gmall.order.interceptor.LoginUserInterceptor;
import com.sherlock.gmall.order.service.OrderItemService;
import com.sherlock.gmall.order.service.OrderService;
import com.sherlock.gmall.order.to.OrderCreateTo;
import com.sherlock.gmall.order.vo.OrderConfirmVo;
import com.sherlock.gmall.order.vo.OrderSubmitVo;
import com.sherlock.gmall.order.vo.SubmitOrderResponseVo;
import feign.RequestInterceptor;
import io.seata.spring.annotation.GlobalTransactional;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private WmsFeignService wmsFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     *   异步线程下ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();获取到空的情况
     *    原因：
     *      本方法在同步调用时，
     *      @see GmallFeignConfig#requestInterceptor() 方法中的
     *      ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes() 代码获取的是同一个线程的数据，
     *      @see org.springframework.web.context.request.RequestContextHolder#getRequestAttributes() 是从 ThreadLocal 中获取数据，但是在异步情况下，运行的不是同一个线程，
     *      所以导致RequestContextHolder.getRequestAttributes()方法获取到null (就是开的另外的线程不能从之前线程获取到信息，所以报空指针)
     *    解决办法：
     *    本方法在Feign远程调用之前，把主线程RequestContextHolder的数据放到子线程
     *    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
     *
     *    CompletableFuture<Void> getCurrentUserCartItems = CompletableFuture.runAsync(() -> {
     *             // 在Feign异步调用之前，把主线程的请求信息同步过来
     *             RequestContextHolder.setRequestAttributes(requestAttributes);
     *         }, executor);
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public OrderConfirmVo confirmOder() throws ExecutionException, InterruptedException {
        // 获取用户
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        // 获取主线程的请求信息
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        OrderConfirmVo confirmVo = new OrderConfirmVo();
        // 1、远程查询所有的收货地址
        CompletableFuture<Void> getAddresses = CompletableFuture.runAsync(() -> {
            // 在Feign异步调用之前，把主线程的请求信息同步过来 (这个请求暂时不需要请求头)
            // RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> addresses = memberFeignService.getAddresses(memberRespVo.getId());
            confirmVo.setAddresses(addresses);
        }, executor);

        // 2、远程查询购物车所有选中的购物项

        CompletableFuture<Void> getCurrentUserCartItems = CompletableFuture.runAsync(() -> {
            // 在Feign异步调用之前，把主线程的请求信息同步过来
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(currentUserCartItems);
        }, executor).thenRunAsync(() -> {
            // 查询库存

            // 在Feign异步调用之前，把主线程的请求信息同步过来 (这个请求暂时不需要请求头)
            // RequestContextHolder.setRequestAttributes(requestAttributes);

            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            ResponseEntity<List<SkuHasStockVo>> skuHasStock = wmsFeignService.getSkuHasStock(skuIds);
            if (skuHasStock.getStatusCode() == HttpStatus.OK && !CollectionUtils.isEmpty(skuHasStock.getBody())) {
                Map<Long, Boolean> hasStocks = skuHasStock.getBody().stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                confirmVo.setStocks(hasStocks);
            }
        }, executor);

        // 3、查询用户的积分信息
        confirmVo.setIntegration(memberRespVo.getIntegration());
        /**
         * Feign远程调用丢失请求头 (Header)
         *
         * Feign在远程调用之前需要构造请求，调用很多的拦截器
         * @see RequestInterceptor interceptor
         *
         * 解决办法
         * @see GmallFeignConfig#requestInterceptor()
         */

        // 4、其他数据自动计算

        /**
         * 订单生成token redis一份， 页面一份， 页面提交时带着然后和redis中的对比，一样就把redis中的删掉， 第二次提交的就说明是重复提交的
         */
        // 5、订单防重令牌
        String token = IdUtil.simpleUUID();
        redisTemplate.opsForValue().set(GmallOrderConstant.GMALL_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        CompletableFuture.allOf(getAddresses, getCurrentUserCartItems).get();
        return confirmVo;
    }

    //@GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo)
    {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        response.setCode(GmallHttpStatus.RESPONSE_OK);
        confirmVoThreadLocal.set(vo);

        // 下单步骤: 验证令牌，创建订单，验价格，锁库存。。。
        String orderToken = vo.getOrderToken();

        // 1、验证令牌【令牌的对比和删除需要保持原子性】
        // 0:删除失败 1:删除成功
        // 原子验证和删除令牌
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(GmallOrderConstant.GMALL_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);

        if (result == 0L) {
            return response.setCode(1);
        } else {
            // 创建订单
            OrderCreateTo orderCreateTo = createOrder();
            // 验价
            BigDecimal payAmount = orderCreateTo.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                // 金额对比成功
                // 保存订单到数据库
                saveOrder(orderCreateTo);
                // 库存锁定
                // 需要订单号，所有订单项(skuId, skuName, num) WareSkuLockVo中都包含
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(orderCreateTo.getOrder().getOrderSn());
                List<OrderItemVo> locks = orderCreateTo.getOrderItems().stream().map(orderItemEntity -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(orderItemEntity.getSkuId());
                    itemVo.setCount(orderItemEntity.getSkuQuantity());
                    itemVo.setTitle(orderItemEntity.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                // 远程锁定库存
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == GmallHttpStatus.RESPONSE_OK) {
                    // 锁定成功
                    response.setOrder(orderCreateTo.getOrder());
                    // 订单创建成功则发送消息给MQ
                    CorrelationData correlationData = new CorrelationData(orderCreateTo.getOrder().getOrderSn());
                    rabbitTemplate.convertAndSend(GmallOrderConstant.ORDER_EVENT_EXCHANGE, GmallOrderConstant.ORDER_CREATE_ORDER_ROUTING_KEY_NAME, orderCreateTo.getOrder(), correlationData);
                    return response;
                } else {
                    // 锁定失败
                    String msg = (String) r.get("msg");
                    throw new NoStockException(msg);
                }
            } else {
                // 验价失败
                throw new CheckPriceDiffException("订单商品价格发生变化，请确认后再次提交");
            }

        }
    }

    @Override
    public OrderEntity getOrderInfoByOrderSn(String orderSn) {
        return getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
    }

    @Override
    public void closeOrder(OrderEntity orderEntity) {
        // 查询订单的最新状态
        OrderEntity entity = getById(orderEntity.getId());
        if (entity.getStatus() == GmallOrderConstant.OrderStatusEnum.CREATE_NEW.getCode()) {
            OrderEntity updateEntity = new OrderEntity();
            updateEntity.setId(orderEntity.getId());
            updateEntity.setStatus(GmallOrderConstant.OrderStatusEnum.CANCLED.getCode());
            updateById(updateEntity);
        }
        /**
         * 发送到订单的交换机， 通过这个路由关系会发送到解锁库存的交换机，直接进行解锁
         *
         * @see com.sherlock.gmall.order.config.OrderMQConfig#orderEventExchange()   交换机
         * @see com.sherlock.gmall.order.config.OrderMQConfig#orderReleaseOtherBinding()  绑定关系
         * @see com.sherlock.gmall.ware.config.WareMQConfig#stockReleaseStockQueue()  队列
         *
          */
        OrderTo orderTo = new OrderTo();
        BeanUtils.copyProperties(entity, orderTo);

        rabbitTemplate.convertAndSend(GmallOrderConstant.ORDER_EVENT_EXCHANGE, GmallOrderConstant.ORDER_RELEASE_OTHER_ROUTING_KEY, orderTo, new CorrelationData(orderEntity.getOrderSn()));
    }


    private OrderCreateTo createOrder(){
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 1、生成订单信息

        // 生成订单号
        String orderSn = IdWorker.getTimeId();

        // 1、构建订单信息
        OrderEntity orderEntity = buildOrder(orderSn);
        orderCreateTo.setOrder(orderEntity);

        // 2、构建所有订单项数据
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);
        orderCreateTo.setOrderItems(orderItemEntities);

        // 3、验价(购物车的总价 和 数据库查询出来的价格进行对比)
        computePrice(orderEntity, orderItemEntities);

        return orderCreateTo;
    }

    /**
     * 构建订单信息
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(memberRespVo.getId());
        // 收货信息
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        //填充收货地址信息
        FareVo fareVo = (FareVo) fare.getData(new TypeReference<FareVo>() {});
        // 运费信息
        orderEntity.setFreightAmount(fareVo.getFare());
        // 收货人信息
        orderEntity.setReceiverCity(fareVo.getAddress().getCity());
        orderEntity.setReceiverName(fareVo.getAddress().getName());
        orderEntity.setReceiverPhone(fareVo.getAddress().getPhone());
        orderEntity.setReceiverPostCode(fareVo.getAddress().getPostCode());
        orderEntity.setReceiverProvince(fareVo.getAddress().getProvince());
        orderEntity.setReceiverRegion(fareVo.getAddress().getRegion());
        orderEntity.setReceiverDetailAddress(fareVo.getAddress().getDetailAddress());
        // 构建订单状态信息
        orderEntity.setStatus(GmallOrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(GmallOrderConstant.GMALL_ORDER_AUTO_CONFIRM_DAY);
        orderEntity.setDeleteStatus(GmallOrderConstant.OrderDeleteStatusEnum.NOT_DELETE.getCode());
        orderEntity.setConfirmStatus(GmallOrderConstant.OrderConfirmStatusEnum.NOT_CONFIRM.getCode());

        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @param orderSn
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 最后一次确认每个购物项的数据
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (!CollectionUtils.isEmpty(currentUserCartItems)) {
            List<OrderItemEntity> collect = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem, orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 构建每一个订单项
     * @param cartItem
     * @param orderSn
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem, String orderSn) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();

        R<SpuInfoVo> infoBySkuId = productFeignService.getSpuInfoBySkuId(cartItem.getSkuId());
        SpuInfoVo data = infoBySkuId.getData(new TypeReference<SpuInfoVo>() {});

        // 1、订单信息：订单号
         orderItemEntity.setOrderSn(orderSn)
        // 2、spu信息
                .setSpuId(data.getId())
                .setSpuName(data.getSpuName())
                .setSpuBrand(data.getBrandName())
                .setCategoryId(data.getCatalogId())
        // 3、sku信息
                .setSkuId(cartItem.getSkuId())
                .setSkuName(cartItem.getTitle())
                .setSkuPic(cartItem.getImage())
                .setSkuPrice(data.getSkuPrice())
                .setSkuAttrsVals(StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(),";"))
                .setSkuQuantity(cartItem.getCount())
        // 4、优惠信息(不做)
        // 5、积分信息
                .setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue())
                .setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue())
        // 6、订单项的价格信息
                .setPromotionAmount(new BigDecimal(0).multiply(new BigDecimal(cartItem.getCount())))
                .setCouponAmount(new BigDecimal(0).multiply(new BigDecimal(cartItem.getCount())))
                .setIntegrationAmount(new BigDecimal(0).multiply(new BigDecimal(cartItem.getCount())));
                 // 当前订单某项的实际金额 - 各种优惠
        BigDecimal orign = data.getSkuPrice().multiply(new BigDecimal(cartItem.getCount()));
        BigDecimal now = orign.subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount())
                .subtract(orderItemEntity.getCouponAmount());
        orderItemEntity.setRealAmount(now);

        return orderItemEntity;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {

        BigDecimal total = new BigDecimal(0);

        BigDecimal couponAmount = new BigDecimal(0);
        BigDecimal integrationAmount = new BigDecimal(0);
        BigDecimal promotionAmount = new BigDecimal(0);

        BigDecimal giftGrowth = new BigDecimal(0);
        BigDecimal giftIntegration = new BigDecimal(0);

        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            total = total.add(orderItemEntity.getRealAmount());

            couponAmount = couponAmount.add(orderItemEntity.getCouponAmount());
            integrationAmount = integrationAmount.add(orderItemEntity.getIntegrationAmount());
            promotionAmount = promotionAmount.add(orderItemEntity.getPromotionAmount());

            giftGrowth = giftGrowth.add(new BigDecimal(orderItemEntity.getGiftGrowth()));
            giftIntegration = giftIntegration.add(new BigDecimal(orderItemEntity.getGiftIntegration()));
        }
        orderEntity.setTotalAmount(total);

        orderEntity.setPromotionAmount(promotionAmount);
        orderEntity.setIntegrationAmount(integrationAmount);
        orderEntity.setCouponAmount(couponAmount);

        // 应付金额 = 物品 + 运费 - 所有优惠
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()).subtract(orderEntity.getPromotionAmount()).subtract(orderEntity.getIntegrationAmount()).subtract(orderEntity.getCouponAmount()));

        orderEntity.setGrowth(giftGrowth.intValue());
        orderEntity.setIntegration(giftIntegration.intValue());

    }

    /**
     * 保存订单数据
     * @param orderTo
     */
    private void saveOrder(OrderCreateTo orderTo) {
        OrderEntity order = orderTo.getOrder();
        order.setModifyTime(new Date());
        save(order);

        List<OrderItemEntity> orderItems = orderTo.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }
}