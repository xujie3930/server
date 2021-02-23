package com.szmsd.common.core.enums;


import lombok.Getter;

/**
 * @FileName ExceptionCodeEnum.java
 * @Description 模块异常编码规则枚举
 * @Date 2020-06-15 14:07
 * @Author Yan Hua
 * @Version 1.0
 */
@Getter
public enum ExceptionCodeEnum {

    /**
     * 基础资料
     */
    BASIS(10, "EXPBASIS"),
    /**
     * 订单管理
     */
    ORDER(20, "EXPORDER"),
    /**
     * 单证管理
     */
    WAYBILL(30, "EXPWAYBILL"),
    /**
     * 财务管理
     */
    FINANCE(40, "EXPFINANCE"),
    /**
     * 报表管理
     */
    REPORT(50, "EXREPORT"),
    /**
     * 出库管理
     */
    SYSTEM(60, "EXPSYSTEM"),
    /**
     * 接口
     */
    INTERFACE(70, "EXPINTERFACE"),
    /**
     * 其他
     */
    OTHER(80, "EXPOTHER");

    private int code;
    private String prefix;

    ExceptionCodeEnum(int code, String prefix) {
        this.code = code;
        this.prefix = prefix;
    }

}