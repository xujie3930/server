package com.szmsd.bas.service;

import com.szmsd.common.core.enums.CodeToNameEnum;

import java.util.Map;

/**
 * @author liyingfeng
 * @date 2020/11/19 10:31
 */
public interface BasCommonService {

    Map<String, Map<String, String>> getCodeToName(CodeToNameEnum type);

    void updateRedis(CodeToNameEnum type);
}
