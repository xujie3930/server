package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 *
 * @author zhangyuyuan
 * @date 2021-03-05 16:48
 */
public enum DelOutboundTrackingAcquireTypeEnum {

    NONE("None", "不获取"),
    ORDER_SUPPLIER("OrderSupplier", "下单后供应商获取"),
    WAREHOUSE_SUPPLIER("WarehouseSupplier", "核重后供应商获取"),

    ;

    private final String code;
    private final String name;

    DelOutboundTrackingAcquireTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundTrackingAcquireTypeEnum get(String code) {
        for (DelOutboundTrackingAcquireTypeEnum anEnum : DelOutboundTrackingAcquireTypeEnum.values()) {
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
