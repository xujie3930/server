package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 13:33
 */
@Data
@Accessors(chain = true)
public class ChargeWrapper implements Serializable {

    // 计价结果信息
    private ShipmentChargeInfo data;

    private ChargeCategory serviceCategory;

    private List<ChargeItem> charges;
}
