package com.szmsd.putinstorage.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 导出SKU
 * @author MSD
 * @date 2021-01-12
 */
@Data
@Accessors(chain = true)
public class InboundReceiptSkuExcelVO implements Serializable {

    private String column0;

    private String column1;

    private String column2;

    private String column3;

    private String column4;

    private String column5;

    private String column6;

}
