package com.szmsd.chargerules.enums;

import lombok.Getter;

@Getter
public enum OrderTypeEnum {

    Shipment("Shipment","出库单"),
    Receipt("Receipt","入库单"),
    Bounce("Bounce","退货单");

    private final String nameEn;

    private final String nameCn;

    OrderTypeEnum(String nameEn,String nameCn) {
        this.nameEn = nameEn;
        this.nameCn = nameCn;
    }

}
