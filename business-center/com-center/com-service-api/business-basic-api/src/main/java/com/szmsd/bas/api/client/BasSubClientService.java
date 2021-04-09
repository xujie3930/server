package com.szmsd.bas.api.client;

import com.szmsd.bas.plugin.vo.BasSubWrapperVO;

import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 18:46
 */
public interface BasSubClientService {

    /**
     * 根据主类别编码查询，主编码多个用,分割
     *
     * @param code code
     * @return Map<String, List < BasSubWrapperVO>>
     */
    Map<String, List<BasSubWrapperVO>> getSub(String code);
}
