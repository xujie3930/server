package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 11:52
 */
@Data
@Accessors(chain = true)
public class ChargeCategory implements Serializable {

    private String billingNo;
    private String chargeNameCN;
    private String chargeNameEN;
    private String parentBillingNo;
}
