package com.szmsd.putinstorage.enums;

import java.util.List;
import java.util.function.Function;

public interface RecordEnum {

    /** 创建，提审，取消，审核，上架，完成 **/
    String getType();

    String getWarehouseNo();

    String getSku();

    /** 内容 **/
    String getContent();

    List<String> getContentFill();

    String getCreateBy();

    String getCreateByName();

    String getCreateTime();

    /** 自定义表达式 **/
    Function<List<String>, String> func();

    default String getFunc(List<String> params) {
        return func().apply(params);
    }

}
