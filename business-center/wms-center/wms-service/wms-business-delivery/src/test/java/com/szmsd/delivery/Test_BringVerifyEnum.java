package com.szmsd.delivery;

import com.szmsd.delivery.service.wrapper.BringVerifyEnum;
import org.junit.Test;

/**
 * @author zhangyuyuan
 * @date 2021-05-07 18:52
 */
public class Test_BringVerifyEnum {

    @Test
    public void testBringVerifyEnum() {
        for (BringVerifyEnum value : BringVerifyEnum.values()) {
            System.out.println(value.ordinal());
        }

        System.out.println(BringVerifyEnum.gt(BringVerifyEnum.FREEZE_INVENTORY, BringVerifyEnum.END));
    }
}
