package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:23
 */
@Data
@ApiModel(value = "出库单明细", description = "DelOutboundDetailDto对象")
public class DelOutboundDetailDto implements Serializable {

    @NotBlank(message = "SKU不能为空")
    @ApiModelProperty(value = "SKU")
    private String sku;

    @NotNull(message = "数量不能为空")
    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "指定编码")
    private String newLabelCode;
}
