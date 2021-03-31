package com.szmsd.inventory.enums;

import lombok.Getter;

@Getter
public enum InventoryStatusEnum {

    PASS("Pass", "通过"),

    REJECT("Reject", "不通过"),

    PENDING("Pending", "待确认");

    private final String code;

    private final String name;

    InventoryStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean checkStatus(String status) {
        for (InventoryStatusEnum value : InventoryStatusEnum.values()) {
            if (value.getCode().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
