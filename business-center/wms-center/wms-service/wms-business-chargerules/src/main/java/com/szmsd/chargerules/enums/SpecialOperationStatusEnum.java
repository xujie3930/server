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

    public Boolean checkStatus(String status) {
        return (SpecialOperationStatusEnum.PASS.getStatus().equals(status)
                || SpecialOperationStatusEnum.REJECT.getStatus().equals(status));
    }

}
