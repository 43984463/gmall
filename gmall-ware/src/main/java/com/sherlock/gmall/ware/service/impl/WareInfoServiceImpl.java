package com.sherlock.gmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.sherlock.common.utils.R;
import com.sherlock.common.vo.MemberAddressVo;
import com.sherlock.gmall.ware.feign.MemberFeignService;
import com.sherlock.common.vo.FareVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.Query;

import com.sherlock.gmall.ware.dao.WareInfoDao;
import com.sherlock.gmall.ware.entity.WareInfoEntity;
import com.sherlock.gmall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(wapper -> {
                wapper.eq("id", key).or().like("name", key).or().like("address", key).or().like("areacode", key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 假设的计算运费
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();

        R r = memberFeignService.addrInfo(addrId);
        MemberAddressVo data = (MemberAddressVo) r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {});
        Random random = new Random();
        int i = random.nextInt(10);
        String substring = String.valueOf(i);
        if (data != null) {
            String phone = data.getPhone();
            if (StringUtils.isNotBlank(phone)) {
               substring = phone.substring(phone.length() - 1);
            }
        }
        fareVo.setFare(new BigDecimal(substring));
        fareVo.setAddress(data);
        return fareVo;
    }

}