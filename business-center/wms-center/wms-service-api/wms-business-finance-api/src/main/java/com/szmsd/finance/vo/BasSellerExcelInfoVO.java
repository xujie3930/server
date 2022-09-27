package com.szmsd.finance.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BasSellerExcelInfoVO implements Serializable {

    private String cusCode;

    private String cusName;

    private String generatorTime;

    private String billTime;

    private String managerNickName;

    private String staffNickName;

}
