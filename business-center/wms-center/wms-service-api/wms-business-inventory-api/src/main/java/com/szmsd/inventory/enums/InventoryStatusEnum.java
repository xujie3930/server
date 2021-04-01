package com.szmsd.inventory.enums;

import lombok.Getter;

@Getter
public enum InventoryStatusEnum {

    PASS(1,"Pass"),

    REJECT(2,"Reject"),

    PENDING(3,"Pending");

    private final int code;

    private final String name;

    InventoryStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static boolean checkStatus(int status) {
        for (InventoryStatusEnum value : InventoryStatusEnum.values()) {
            if (value.getCode() == status) {
                return true;
            }
        }
        return false;
    }
}
