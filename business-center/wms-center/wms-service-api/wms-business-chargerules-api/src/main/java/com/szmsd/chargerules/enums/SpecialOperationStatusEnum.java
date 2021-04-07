package com.szmsd.chargerules.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SpecialOperationStatusEnum {

    PASS(1,"Pass"),

    REJECT(2,"Reject"),

    PENDING(3,"Pending");

    @EnumValue
    private final Integer status;

    private final String statusName;

    SpecialOperationStatusEnum(Integer status,String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    public static Boolean checkStatus(Integer status) {
        return get(status) != null;
    }

    public static SpecialOperationStatusEnum get(Integer code) {
        for (SpecialOperationStatusEnum statusEnum : SpecialOperationStatusEnum.values()) {
            if (statusEnum.getStatus().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

}
