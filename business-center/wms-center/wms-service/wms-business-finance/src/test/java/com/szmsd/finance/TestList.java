package com.szmsd.finance;

import com.alibaba.fastjson.JSON;
import com.szmsd.finance.domain.ExchangeRate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TestList {

    public static void main(String[] args) {

        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setExchangeFromCode("USD");
        exchangeRate.setExchangeToCode("CNY");

        ExchangeRate exchangeRate1 = new ExchangeRate();
        exchangeRate1.setExchangeFromCode("USD");
        exchangeRate1.setExchangeToCode("CNY");

        exchangeRateList.add(exchangeRate1);
        exchangeRateList.add(exchangeRate);

        List<ExchangeRate> secondMenus = exchangeRateList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(o->o.getExchangeFromCode()+";"+o.getExchangeToCode()))), ArrayList::new));

        System.out.println(JSON.toJSONString(secondMenus));
    }
}
