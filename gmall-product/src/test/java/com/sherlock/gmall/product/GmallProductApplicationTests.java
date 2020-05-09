package com.sherlock.gmall.product;

import com.sherlock.gmall.product.entity.BrandEntity;
import com.sherlock.gmall.product.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @auther Sherlock
 * @date 2020/5/9 1:11
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GmallProductApplicationTests {


    @Autowired
    private BrandService brandService;

    @Test
    public void contextLoads(){

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        log.info("保存成功");
    }

}
