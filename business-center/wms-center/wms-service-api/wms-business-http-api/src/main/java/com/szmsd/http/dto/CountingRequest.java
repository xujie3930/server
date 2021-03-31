package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountingRequest {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "OMS的盘点单号")
    private String refOrderNo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "Sku列表")
    private List<String> details;

}
