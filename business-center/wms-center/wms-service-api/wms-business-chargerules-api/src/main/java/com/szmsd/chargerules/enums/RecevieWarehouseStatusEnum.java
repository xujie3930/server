package com.szmsd.chargerules.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RecevieWarehouseStatusEnum {

    WAREHOUSESTATUS(1, "是否收货仓库");
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
