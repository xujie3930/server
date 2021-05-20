package com.szmsd.inventory.enums;

import lombok.Getter;

@Getter
public enum InventoryInspectionEnum {

    NEW_SKU_REQ("081001","新SKU必验"),

    ALL_REQ("081002","入库必验货"),

    NO_REQ("081003","无需验货");

    private final String code;

    private final String name;

    InventoryInspectionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
