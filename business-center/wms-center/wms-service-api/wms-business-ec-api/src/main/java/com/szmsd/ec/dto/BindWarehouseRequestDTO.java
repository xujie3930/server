package com.szmsd.ec.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class BindWarehouseRequestDTO {

    @Size(min = 1, message = "选择一条或多条数据")
    @ApiModelProperty("列表id 多选")
    private List<Long> ids;

    @NotBlank(message = "仓库Id不能为空")
    @ApiModelProperty("shopify仓库Id")
    private String warehouseId;

    @NotBlank(message = "仓库名称不能为空")
    @ApiModelProperty("shopify仓库名称")
    private String warehouseName;
}
