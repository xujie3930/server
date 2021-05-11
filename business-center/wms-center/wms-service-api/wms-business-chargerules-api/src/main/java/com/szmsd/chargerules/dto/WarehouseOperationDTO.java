package com.szmsd.chargerules.dto;

import com.szmsd.chargerules.domain.WarehouseOperationDetails;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "WarehouseOperationDTO", description = "WarehouseOperationDTO对象")
public class WarehouseOperationDTO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "详情")
    private List<WarehouseOperationDetails> details;



}
