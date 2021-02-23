package com.szmsd.common.core.exception.com;

import com.szmsd.common.core.enums.ExceptionMessageEnum;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

public abstract class AssertUtil extends Assert {

    public static void isTrue(boolean expression, ExceptionMessageEnum exceptionMessageEnum) {
        if (!expression) {
            throw LogisticsExceptionUtil.getException(exceptionMessageEnum, getLen());
        }
    }

    public static void isTrue(boolean expression, ExceptionMessageEnum exceptionMessageEnum, Object... values) {
        if (!expression) {
            LogisticsException exception = LogisticsExceptionUtil.getException(exceptionMessageEnum, getLen(), values);
            throw exception;
        }
    }

    public static void isNull(@Nullable Object object, ExceptionMessageEnum exceptionMessageEnum) {
        if (object != null) {
            throw LogisticsExceptionUtil.getException(exceptionMessageEnum, getLen());
        }
    }

    public static void isNull(@Nullable Object object, ExceptionMessageEnum exceptionMessageEnum, Object... values) {
        if (object != null) {
            throw LogisticsExceptionUtil.getException(exceptionMessageEnum, getLen(), values);
        }
    }

    public static void notNull(@Nullable Object object, ExceptionMessageEnum exceptionMessageEnum) {
        if (object == null) {
            throw LogisticsExceptionUtil.getException(exceptionMessageEnum, getLen());
        }
    }

    public static void notNull(@Nullable Object object, ExceptionMessageEnum exceptionMessageEnum, Object... values) {
        if (object == null) {
            throw LogisticsExceptionUtil.getException(exceptionMessageEnum, getLen(), values);
        }
    }
}
