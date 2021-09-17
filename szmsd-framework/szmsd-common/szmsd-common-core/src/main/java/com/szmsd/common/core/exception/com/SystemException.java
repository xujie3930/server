package com.szmsd.common.core.exception.com;

import com.szmsd.common.core.enums.BaseEnum;

public class SystemException extends BaseException {

    private static final long serialVersionUID = -1L;

    public SystemException(String code) {
        super(code, null, null, null);
    }

    public SystemException(String code, String message) {
        super(code, message, null, null);
    }

    public SystemException(String code, String message, Object[] values) {
        super(code, message, null, values);
    }

    public SystemException(String code, String message, Throwable throwable, Object[] values) {
        super(code, message, throwable, values);
    }

    public SystemException(BaseEnum error) {
        super(error.getKey(), error.getValue(), null, null);
    }

}
