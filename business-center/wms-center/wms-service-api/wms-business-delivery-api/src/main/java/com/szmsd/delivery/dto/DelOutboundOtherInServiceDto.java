package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:23
 */
@Data
@ApiModel(value = "DelOutboundOtherInServiceDto", description = "DelOutboundOtherInServiceDto对象")
public class DelOutboundOtherInServiceDto implements Serializable {

    @NotBlank(message = "客户代码不能为空")
    @ApiModelProperty(value = "客户代码")
    private String clientCode;

    @NotBlank(message = "仓库编码不能为空")
    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @NotBlank(message = "国家编码不能为空")
    @ApiModelProperty(value = "国家编码")
    private String countryCode;

    @NotNull(message = "SKU不能为空")
    @ApiModelProperty(value = "SKU")
    private List<String> skus;
}
