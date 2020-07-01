package com.sherlock.gmall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @auther Sherlock
 * @date 2020/6/20 11:57
 * @Description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {

    private String catalog1Id;
    private List<Catelog3Vo> catalog3List;
    private String id;
    private String name;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}

