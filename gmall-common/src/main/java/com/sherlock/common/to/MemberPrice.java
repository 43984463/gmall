/**
  * Copyright 2020 bejson.com 
  */
package com.sherlock.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;
    private List<MemberPrice> memberPrice;

}