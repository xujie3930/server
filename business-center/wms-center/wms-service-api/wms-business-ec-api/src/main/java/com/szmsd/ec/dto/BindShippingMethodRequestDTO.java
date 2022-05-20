package com.szmsd.ec.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class BindShippingMethodRequestDTO {

    @Size(min = 1, message = "选择一条或多条数据")
    @ApiModelProperty("列表id 多选")
    private List<Long> ids;

    @NotBlank(message = "发货方式编码不能为空")
    @ApiModelProperty("发货方式编码")
    private String shippingMethodCode;

    @NotBlank(message = "发货方式名称不能为空")
    @ApiModelProperty("发货方式名称")
    private String shippingMethod;
}
