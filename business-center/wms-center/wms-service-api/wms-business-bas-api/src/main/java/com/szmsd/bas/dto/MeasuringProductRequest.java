package com.szmsd.bas.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MeasuringProductRequest {
    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "产品编码")
    @NotBlank(message = "sku代码不能为空")
    @TableField("`code`")
    private String code;

    @ApiModelProperty(value = "仓库测量重量g")
    @NotBlank(message = "仓库测量重量不能为空")
    private Double weight;

    @ApiModelProperty(value = "仓库测量长 cm")
    @NotBlank(message = "仓库测量长不能为空")
    private Double length;

    @ApiModelProperty(value = "仓库测量宽 cm")
    @NotBlank(message = "仓库测量宽不能为空")
    private Double width;

    @ApiModelProperty(value = "仓库测量高 cm")
    @NotBlank(message = "仓库测量高不能为空")
    private Double height;

    @ApiModelProperty(value = "关联单号")
    private String orderNo;
}
