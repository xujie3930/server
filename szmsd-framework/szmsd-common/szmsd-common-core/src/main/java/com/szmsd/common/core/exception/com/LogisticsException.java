package com.szmsd.common.core.exception.com;

/**
 * @FileName LogisticsExceptionUtil.java
 * @Description 异常信息处理类
 * @Date 2020-06-15 14:07
 * @Author Yan Hua
 * @Version 1.0
 */
public class LogisticsException extends CommonException {

    private static final long serialVersionUID = 1620521772541012990L;

    /**
     * @param code    异常码
     * @param message 异常信息
     * @param cause   异常
     * @param values  参数
     */
    public LogisticsException(String code, String message, Throwable cause, Object[] values) {
        super(code, message, cause, values);
    }

    /**
     * @param code    异常码
     * @param message 异常信息
     * @param values  参数
     */
    public LogisticsException(String code, String message, Object[] values) {
        super(code, message, values);
    }

    /**
     * @param code    异常码
     * @param message 异常信息
     */
    public LogisticsException(String code, String message) {
        super(code, message);
    }

    /**
     * @param code 异常码
     */
    public LogisticsException(String code) {
        super(code);
    }

}
