package com.szmsd.chargerules.enums;

public enum SpecialOperationStatusEnum {

    PASS("Pass"),

    REJECT("Reject");

    private final String status;

    SpecialOperationStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
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
