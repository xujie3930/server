package com.szmsd.gateway.service;

import java.io.IOException;

import com.szmsd.common.core.domain.R;

/**
 * 验证码处理
 *
 * @author szmsd
 */
public interface ValidateCodeService {

    /**
     * 生成验证码
     */
    R createCapcha() throws Exception;

    /**
     * 校验验证码
     */
    void checkCapcha(String key, String value) throws Exception;


}
