package com.szmsd.delivery.enums;

/**
 * @author zhangyuyuan
 * @date 2021-04-06 15:24
 */
public enum OfflineDeliveryStateEnum {

    INIT("INIT", "初始化"),
    CREATE_ORDER("CREATE_ORDER","创建订单"),
    CREATE_COST("CREATE_COST", "创建费用"),
    PUSH_TY("PUSH_TY","推送TY"),
    COMPLETED("COMPLETED","完成")
    ;

    private final String code;
    private final String name;

    OfflineDeliveryStateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
