package com.szmsd.delivery.dto;

import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
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
@ApiModel(value = "DelOutboundDetailDto", description = "DelOutboundDetailDto对象")
public class DelOutboundDetailDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @NotBlank(message = "SKU不能为空", groups = {ValidationSaveGroup.class, ValidationUpdateGroup.class})
    @ApiModelProperty(value = "SKU")
    private String sku;

    @NotNull(message = "数量不能为空", groups = {ValidationSaveGroup.class, ValidationUpdateGroup.class})
    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "指定编码")
    private String newLabelCode;

    @ApiModelProperty(value = "行号")
    private Long lineNo;

    @ApiModelProperty(value = "长 - 用于计算不保存")
    private Double length;

    @ApiModelProperty(value = "宽 - 用于计算不保存")
    private Double width;

    @ApiModelProperty(value = "高 - 用于计算不保存")
    private Double height;

    @ApiModelProperty(value = "重量 - 用于计算不保存")
    private Double weight;
}
