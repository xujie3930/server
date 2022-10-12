package com.szmsd.finance;

import cn.hutool.core.util.RandomUtil;
import com.szmsd.common.core.utils.DateUtils;

public class TestSerinumber {

    public static void main(String[] args) {

        for(int i =0;i<10;i++) {
            String s = DateUtils.dateTime();
            String randomNums = RandomUtil.randomNumbers(8);
            System.out.println(s + randomNums);
        }

    }
}
