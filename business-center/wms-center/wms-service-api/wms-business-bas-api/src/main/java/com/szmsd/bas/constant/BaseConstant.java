package com.szmsd.bas.constant;

import java.util.concurrent.TimeUnit;

public class BaseConstant {

    public static final Long LOCK_TIME = 30L;
    public static final TimeUnit LOCK_TIME_UNIT = TimeUnit.SECONDS;

    public static String genErrorMsg(String errorMsg) {
        return String.format("%s请求超时,请稍候重试!",errorMsg);
    }


}