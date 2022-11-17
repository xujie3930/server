package com.szmsd.chargerules.service;

import com.szmsd.chargerules.domain.BasProductService;

import java.util.List;

public interface IBasProductService {

    /**
     * 根据产品代码查询产品服务
     * @param productCodeList
     * @return
     */
    public List<BasProductService> selectBasProductByCode(List<String> productCodeList);
}
