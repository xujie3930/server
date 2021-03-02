package com.szmsd.bas.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author liyingfeng
 * @date 2020/11/19 10:34
 */
@Mapper
public interface BasCommonMapper {
    List<Map<String, String>> getCustomer();

    List<Map<String, String>> getBasEmployees();

    List<Map<String, String>> getBasProductType();

    List<Map<String, String>> getCountry();

    List<Map<String, String>> getBasSub();

    List<Map<String, String>> getCity();

    List<Map<String, String>> getProvince();

}
