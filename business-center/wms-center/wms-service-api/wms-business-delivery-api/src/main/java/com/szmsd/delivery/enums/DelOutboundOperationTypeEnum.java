package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 16:22
 */
public enum DelOutboundOperationTypeEnum {

    PROCESSING("Processing", "开始处理"),
    SHIPPED("Shipped", "已发货"),
    CANCELED("Canceled", "已取消"),

    // 其它业务操作的枚举
    BRING_VERIFY("BringVerify", "提审"),
    SHIPMENT_PACKING("ShipmentPacking", "包裹核重"),

    ;

    private final String code;
    private final String name;

    DelOutboundOperationTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundOperationTypeEnum get(String code) {
        for (DelOutboundOperationTypeEnum anEnum : DelOutboundOperationTypeEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public static boolean has(String code) {
        return Objects.nonNull(get(code));
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
