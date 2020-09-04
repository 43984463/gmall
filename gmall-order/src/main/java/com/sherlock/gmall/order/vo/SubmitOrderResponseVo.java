package com.sherlock.gmall.order.vo;

import com.sherlock.gmall.order.entity.OrderEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @auther Sherlock
 * @date 2020/9/4 20:15
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SubmitOrderResponseVo {

    private OrderEntity order;
    private Integer code; // 0:成功

}
