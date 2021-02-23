package com.szmsd.common.core.exception.user;

import com.szmsd.common.core.exception.web.BaseException;

/**
 * 用户信息异常类
 * 
 * @author szmsd
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
