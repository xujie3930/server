package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 13:34
 */
@Data
@Accessors(chain = true)
public class ChargeItem implements Serializable {

    private ChargeCategory chargeCategory;

    private Money money;

    private String remark;
}
