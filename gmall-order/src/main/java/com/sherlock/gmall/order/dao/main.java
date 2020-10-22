package com.sherlock.gmall.order.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/9/6
 **/
public class main {
    public static void main(String[] args) {
        List<Test> list = new ArrayList<>();
        Test test1 = new Test("1", new BigDecimal(20.54));
        Test test2 = new Test("2", new BigDecimal(21));
        Test test3 = new Test("1", new BigDecimal(23));
        Test test4 = new Test("3", new BigDecimal(24));
        Test test5 = new Test("4", new BigDecimal(25));
        Test test6 = new Test("5", new BigDecimal(26));
        Test test7 = new Test("6", new BigDecimal(27));
        Test test8 = new Test("3", new BigDecimal(28));
        list.add(test1);
        list.add(test2);
        list.add(test3);
        list.add(test4);
        list.add(test5);
        list.add(test6);
        list.add(test7);
        list.add(test8);
        Map<String, Double> collect = list.stream().collect(Collectors.groupingBy(Test::getId, Collectors.summingDouble(test -> test.getValue().doubleValue())));
        //System.out.println(collect);


        DateTimeFormatter yyyymMdd = DateTimeFormatter.ofPattern("YYYYMMdd");
        //System.out.println(yyyymMdd.format(LocalDate.now()));


        String ss = "Hello";
        String[] split = ss.split("");
        System.out.println(split.toString());
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Test{
    private String id;
    private BigDecimal value;
}