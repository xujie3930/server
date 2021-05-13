package com.szmsd.delivery.constant;

import com.szmsd.bas.constant.ShipmentType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-05-12 17:16
 */
public class Test_ShipmentType {

    @Test
    public void test() {

        List<String> list = new ArrayList<>();
        list.add("Battery");

        System.out.println(ShipmentType.highest(list));
    }
}
