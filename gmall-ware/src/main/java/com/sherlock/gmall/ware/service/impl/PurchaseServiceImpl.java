package com.sherlock.gmall.ware.service.impl;

import com.sherlock.common.constants.GmallWareConstant;
import com.sherlock.gmall.ware.entity.PurchaseDetailEntity;
import com.sherlock.gmall.ware.entity.WareSkuEntity;
import com.sherlock.gmall.ware.service.PurchaseDetailService;
import com.sherlock.gmall.ware.service.WareSkuService;
import com.sherlock.gmall.ware.vo.MergeVo;
import com.sherlock.gmall.ware.vo.PurchaseDoneVo;
import com.sherlock.gmall.ware.vo.PurchaseItemDoneVo;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.ware.dao.PurchaseDao;
import com.sherlock.gmall.ware.entity.PurchaseEntity;
import com.sherlock.gmall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (mergeVo.getPurchaseId() == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date()).setUpdateTime(new Date());
            purchaseEntity.setStatus(GmallWareConstant.PurchaseStatusEnum.CREATED.getCode());
            save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> detailEntities = items.stream().map(item -> {
            PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item);
            if (detailEntity.getStatus() == GmallWareConstant.PurchaseDetailStatusEnum.CREATED.getCode()
                    || detailEntity.getStatus() == GmallWareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()) {
                detailEntity.setId(item);
                detailEntity.setPurchaseId(finalPurchaseId);
                detailEntity.setStatus(GmallWareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            }
            return detailEntity;
            // 过滤掉提交过来的已完成或者正在采购或者采购失败的采购单 只修改新建或者已分配的采购单
        }).filter(item -> item.getStatus() == GmallWareConstant.PurchaseDetailStatusEnum.CREATED.getCode()
                || item.getStatus() == GmallWareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode())
                .collect(Collectors.toList());

        purchaseDetailService.updateBatchById(detailEntities);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);
    }

    @Override
    public void receivePurchase(List<Long> ids) {
        //1、确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item ->
                item.getStatus() == GmallWareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                        item.getStatus() == GmallWareConstant.PurchaseStatusEnum.ASSIGNED.getCode()
        ).map(item -> {
            item.setStatus(GmallWareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //2、改变采购单的状态
        updateBatchById(collect);

        //3、改变采购项的状态
        collect.forEach((item) -> {
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = entities.stream().map(entity -> {
                PurchaseDetailEntity entity1 = new PurchaseDetailEntity();
                entity1.setId(entity.getId());
                entity1.setStatus(GmallWareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity1;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }

    @Transactional
    @Override
    public void purchaseDone(PurchaseDoneVo vo) {
        boolean haveError = false;
        // 改变采购单状态
        Long id = vo.getId();

        // 改变所有采购项的状态
        List<PurchaseItemDoneVo> items = vo.getItems();
        List<PurchaseDetailEntity> needUpdatePurchaseDetail = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == GmallWareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getCode()) {
                haveError = true;
                detailEntity.setStatus(item.getStatus());
            } else {
                detailEntity.setStatus(GmallWareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                // 将采购成功的入库
                PurchaseDetailEntity detailServiceById = purchaseDetailService.getById(item.getItemId());
                wareSkuService.updateStock(detailServiceById.getSkuId(), detailServiceById.getWareId(), detailServiceById.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            needUpdatePurchaseDetail.add(detailEntity);
        }

        purchaseDetailService.updateBatchById(needUpdatePurchaseDetail);

        // 改变采购单的状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        if (haveError) {
            purchaseEntity.setStatus(GmallWareConstant.PurchaseStatusEnum.HAS_ERROR.getCode());
        } else {
            purchaseEntity.setStatus(GmallWareConstant.PurchaseStatusEnum.FINISH.getCode());
        }
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);

    }

}