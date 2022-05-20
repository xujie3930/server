package com.szmsd.ec.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class BindShippingServiceRequestDTO {

    @Size(min = 1, message = "选择一条或多条数据")
    @ApiModelProperty("列表id 多选")
    private List<Long> ids;

    @NotBlank(message = "发货仓库编码不能为空")
    @ApiModelProperty(value = "发货仓库")
    private String WarehouseCode;

    @NotBlank(message = "发货仓库名称不能为空")
    @ApiModelProperty(value = "发货仓库名称")
    @Excel(name = "发货仓库名称")
    private String WarehouseName;

    @NotBlank(message = "发货服务编码不能为空")
    @ApiModelProperty("发货服务编码")
    private String shippingServiceCode;

    @NotBlank(message = "发货服务名称不能为空")
    @ApiModelProperty("发货服务名称")
    private String shippingService;
}
