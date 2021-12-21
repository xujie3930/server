package com.szmsd.delivery;

import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import org.junit.Test;

public class TestDelCk1RequestLogConstant {

    @Test
    public void testType() {
        DelCk1RequestLogConstant.Type create = DelCk1RequestLogConstant.Type.valueOf("create");

        System.out.println(create.ordinal());

        System.out.println(create.getCallbackService());
    }
}
