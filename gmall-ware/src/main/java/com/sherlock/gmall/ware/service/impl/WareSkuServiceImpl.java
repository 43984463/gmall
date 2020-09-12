package com.sherlock.gmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.constants.GmallOrderConstant;
import com.sherlock.common.constants.GmallWareConstant;
import com.sherlock.common.exception.GmallHttpStatus;
import com.sherlock.common.exception.NoStockException;
import com.sherlock.common.to.SkuHasStockVo;
import com.sherlock.common.to.mq.OrderTo;
import com.sherlock.common.to.mq.StockLockedTo;
import com.sherlock.common.to.mq.WareOrderStockLockedDetailTo;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.OrderItemVo;
import com.sherlock.common.vo.SkuInfoVo;
import com.sherlock.common.vo.WareSkuLockVo;
import com.sherlock.gmall.ware.dao.WareSkuDao;
import com.sherlock.gmall.ware.entity.WareOrderTaskDetailEntity;
import com.sherlock.gmall.ware.entity.WareOrderTaskEntity;
import com.sherlock.gmall.ware.entity.WareSkuEntity;
import com.sherlock.gmall.ware.feign.OrderFeignService;
import com.sherlock.gmall.ware.feign.ProductFeignService;
import com.sherlock.gmall.ware.service.WareOrderTaskDetailService;
import com.sherlock.gmall.ware.service.WareOrderTaskService;
import com.sherlock.gmall.ware.service.WareSkuService;
import com.sherlock.gmall.ware.vo.OrderVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(skuId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateStock(Long skuId, Long wareId, Integer skuNum) {
        WareSkuEntity entity = getOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entity == null) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStockLocked(0);
            R<SkuInfoVo> info = productFeignService.getSkuInfo(skuId);
            // 远程获取失败不影响整个商品信息保存
            try {
                if (info.getCode() == GmallHttpStatus.RESPONSE_OK) {
                    //Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    SkuInfoVo skuInfo = info.getData(new TypeReference<SkuInfoVo>() {});
                    wareSkuEntity.setSkuName(skuInfo.getSkuName());
                }
            } catch (Exception e) {
                log.warn("远程获取商品信息失败");
            }
            save(wareSkuEntity);
        } else {
            wareSkuDao.updateStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            long count = wareSkuDao.getSkuSotck(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count > 0);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 锁定商品
     *
     * @param lockVo
     * @return 锁定某个订单的库存
     * <p>
     * 库存解锁场景：
     * 1)、下单成功，订单过期没有支付被系统自动取消、被用户手动取消
     * 2)、下订单成功，库存锁定成功，但是后面的业务调用失败了，导致订单回滚，那么之前锁定的库存就要自动解锁。
     *
     * <p>
     * <p>
     * 默认运行时异常全都回滚
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean orderLockStock(WareSkuLockVo lockVo) {

        /**
         * 保存库存工作单详情
         */

        WareOrderTaskEntity wareSkuEntity = new WareOrderTaskEntity();
        wareSkuEntity.setOrderSn(lockVo.getOrderSn());
        wareOrderTaskService.save(wareSkuEntity);


        // TODO 1、按照下单地址，找到附近仓库，锁定库存
        List<OrderItemVo> locks = lockVo.getLocks();

        // 1、找到每个商品在哪个仓库都有库存
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            // 查询哪个库存包含这个商品
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            stock.setNum(item.getCount());
            stock.setSkuName(item.getTitle());
            return stock;
        }).collect(Collectors.toList());

        // 2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuLocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (CollectionUtils.isEmpty(wareIds)) {
                throw new NoStockException(skuId);
            }
            /**
             *
             * 如果每一个商品都锁定成功，将当前商品锁定的详情信息工作单发送给MQ
             *
             * 锁定解锁2种情况：
             * 1）、下订单成功，订单过期没有支付被系统自动取消、或者被用户手动取消。都需要解锁库存
             * 2）、下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。之前的库存需要解锁
             *
             * 前面保存的工作信息单就回滚。发送出去的消息，即使要解锁记录，需要去数据库根据ID查询，所以就不用解锁
             *  详情不能只发送ID，不然回滚的时候不知道锁定了几件和仓库ID等其他信息
             */
            for (Long wareId : wareIds) {
                // 成功返回1 (更新数据库一条数据，当前商品在这个仓库中有并且被锁住了) 否则为0
                long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    skuLocked = true;
                    // 保存信息到数据库
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
                    detailEntity.setSkuId(skuId)
                            .setSkuNum(hasStock.getNum())
                            .setTaskId(wareSkuEntity.getId())
                            .setWareId(wareId)
                            .setSkuName(hasStock.getSkuName())
                            .setLockStatus(1);
                    wareOrderTaskDetailService.save(detailEntity);
                    // 告诉MQ锁定成功
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    WareOrderStockLockedDetailTo wareOrderStockLockedDetailTo = new WareOrderStockLockedDetailTo();
                    BeanUtils.copyProperties(detailEntity, wareOrderStockLockedDetailTo);
                    stockLockedTo.setId(wareSkuEntity.getId()).setDetail(wareOrderStockLockedDetailTo);
                    // 发送给MQ的死信路由 2分钟之后到锁仓库的queue
                    CorrelationData correlationData = new CorrelationData(lockVo.getOrderSn());
                    rabbitTemplate.convertAndSend(GmallWareConstant.STOCK_EVENT_EXCHANGE_NAME, "stock.locked", stockLockedTo, correlationData);
                    break;
                } else {
                    // 当前仓库锁失败(没有足够的货)，重试下一个仓库

                }
            }
            if (skuLocked == false) {
                // 当前商品在所有仓库中都没有被锁住(货源不足)
                throw new NoStockException(skuId);
            }
        }

        // 代码走到这说明全都锁定成功

        return true;
    }


    @Transactional
    @Override
    public void unLockStock(StockLockedTo stockLockedTo) {

        WareOrderStockLockedDetailTo detail = stockLockedTo.getDetail();
        wareSkuDao.unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum());

        // 修改为已解锁
        WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
        detailEntity.setId(detail.getId());
        detailEntity.setLockStatus(GmallWareConstant.WareOrderTaskDetailStatusEnum.UNLOCK.getCode());
        wareOrderTaskDetailService.updateById(detailEntity);
    }

    @Transactional
    @Override
    public void orderUnLockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        // 解锁库存
        WareOrderTaskEntity taskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        // 按照工作单找到所有的没有解锁的库存， 进行解锁
        List<WareOrderTaskDetailEntity> detailEntities = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", taskEntity.getId())
                .eq("lock_status", GmallWareConstant.WareOrderTaskDetailStatusEnum.LOCKED.getCode()));

        for (WareOrderTaskDetailEntity detailEntity : detailEntities) {
            StockLockedTo stockLockedTo = new StockLockedTo();
            WareOrderStockLockedDetailTo detail = new WareOrderStockLockedDetailTo();
            detail.setSkuId(detailEntity.getSkuId());
            detail.setId(detailEntity.getId());
            detail.setWareId(detailEntity.getWareId());
            detail.setSkuNum(detailEntity.getSkuNum());
            stockLockedTo.setDetail(detail);
            // 本类调用
            WareSkuServiceImpl wareSkuServiceImpl = (WareSkuServiceImpl) AopContext.currentProxy();
            wareSkuServiceImpl.unLockStock(stockLockedTo);
        }

    }

    @Override
    public void orderUnLockStock(StockLockedTo stockLockedTo) {
        log.info("解锁库存：{}", stockLockedTo);
        // 解锁时先去库存查询
        WareOrderStockLockedDetailTo detail = stockLockedTo.getDetail();
        // 查询库存关于这个订单的库存信息
        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(detail.getId());
        if (detailEntity != null) {
            /**
             *  查到了 库存锁定成功
             *
             *  接下来需要查看订单情况
             *  1）、查不到订单 。说明订单回滚， 需要解锁库存
             *  2）、有订单
             *       查看订单状态
             *       1）、(系统自动)取消状态， 解锁库存
             *       2）、没有取消，不能解锁
             *
             */
            Long id = stockLockedTo.getId();
            WareOrderTaskEntity orderTask = wareOrderTaskService.getById(id);
            R orderInfoByOrderSn = orderFeignService.getOrderInfoByOrderSn(orderTask.getOrderSn());
            if (orderInfoByOrderSn.getCode() == GmallHttpStatus.RESPONSE_OK) {
                // 订单数据返回成功
                OrderVo orderVo = (OrderVo) orderInfoByOrderSn.getData(new TypeReference<OrderVo>() {
                });
                // 订单不存在
                // 或者
                // 订单被取消
                // 解锁库存
                if ((orderVo == null || orderVo.getStatus() == GmallOrderConstant.OrderStatusEnum.CANCLED.getCode()
                        && detailEntity.getLockStatus() == GmallWareConstant.WareOrderTaskDetailStatusEnum.LOCKED.getCode())) {
                        // 库存工作单详情中，状态为1-已锁定但是未解锁 才去解锁
                        unLockStock(stockLockedTo);
                }
            } else {
                // 消息拒绝之后重新放回队列，别人可以重新解锁
                throw new RuntimeException("远程调用失败，重新解锁");
            }

        } else {
            // 没查到 不需要解锁
        }

    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private String skuName;
        private Integer num;
        private List<Long> wareId;
    }

}