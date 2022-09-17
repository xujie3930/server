package com.szmsd.delivery;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {

    @Test
    public void beanCopy() {

        BigDecimal rate = new BigDecimal(1000);
        BigDecimal b = new BigDecimal(0.1432);

        BigDecimal addAmount = rate.multiply(b).setScale(2, BigDecimal.ROUND_FLOOR);

        System.out.println(addAmount);
    }
}
