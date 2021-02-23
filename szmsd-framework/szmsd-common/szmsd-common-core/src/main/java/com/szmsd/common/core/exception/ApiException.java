package com.szmsd.common.core.exception;

/**
 * 自定义异常 返回精准code码
 *
 * @author szmsd
 */
public class ApiException extends Exception
{
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    public ApiException(String message)
    {
        this.message = message;
    }

    public ApiException(String message, Integer code)
    {
        this.message = message;
        this.code = code;
    }

    public ApiException(String message, Throwable e)
    {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public Integer getCode()
    {
        return code;
    }
}
