package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class InboundInventoryInspectionDTO {

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "入库sku")
    private List<String> skus;

}
