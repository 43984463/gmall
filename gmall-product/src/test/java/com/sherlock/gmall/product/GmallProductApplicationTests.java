package com.sherlock.gmall.product;

import com.sherlock.gmall.product.entity.BrandEntity;
import com.sherlock.gmall.product.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;
   /* @Autowired
    private OSS ossClient;

    @Test
    public void uploadFileToAliOSS() throws FileNotFoundException {

        InputStream is = new FileInputStream("C:\\Users\\Administrator\\Desktop\\redis.conf");
        ossClient.putObject("gmall-sherlock", "redisConfigFile", is);
        System.out.println("上传成功");
    }*/

    @Test
    public void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        log.info("保存成功");
    }


    @Test
    public void testRedis() {

        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world" + UUID.randomUUID().toString());

        System.out.println("hello的值是" + ops.get("hello"));
    }

    @Test
    public void testRedisson() {
        System.out.println(redissonClient);
    }
}
