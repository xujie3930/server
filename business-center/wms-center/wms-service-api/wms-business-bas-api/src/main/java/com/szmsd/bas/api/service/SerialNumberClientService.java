package com.szmsd.bas.api.service;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2020-12-02 002 9:41
 */
public interface SerialNumberClientService {

    /**
     * 生成流水号
     *
     * @param code code
     * @return String
     */
    String generateNumber(String code);

    /**
     * 生成流水号
     *
     * @param code code
     * @param num  num
     * @return String
     */
    List<String> generateNumbers(String code, int num);
}
