package com.szmsd.chargerules.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SpecialOperationStatusEnum {

    PASS("Pass","通过"),

    REJECT("Reject","不通过"),

    PENDING("Pending","待确认");

    @EnumValue
    private final String status;

    private final String statusName;

    SpecialOperationStatusEnum(String status,String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    public static Boolean checkStatus(String status) {
        return get(status) != null;
    }

    public static SpecialOperationStatusEnum get(String code) {
        for (SpecialOperationStatusEnum statusEnum : SpecialOperationStatusEnum.values()) {
            if (statusEnum.getStatus().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

}
