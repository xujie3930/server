package com.szmsd.chargerules.enums;

public enum ErrorMessageEnum {

    STATUS_RESULT("请确认审核结果"),

    COEFFICIENT_IS_ZERO("审批通过系数必须大于0"),

    ORDER_IS_NOT_EXIST("订单不存在"),

    OPERATION_TYPE_NOT_FOUND("未找到该特殊操作的操作类型配置"),

    WAREHOUSE_PRICE_NOT_FOUND("未找到该储存仓租的计费配置"),

    PAY_FAILED("扣款失败"),

    UPDATE_OPERATION_TYPE_ERROR("更新远程特殊操作结果失败");

    private final String message;

    ErrorMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
