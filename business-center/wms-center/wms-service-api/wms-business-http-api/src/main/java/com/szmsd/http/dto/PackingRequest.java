package com.szmsd.http.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PackingRequest {
    @ApiModelProperty(value = "物料名称")
    @Excel(name = "物料名称")
    private String name;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String code;

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String category;

    @ApiModelProperty(value = "价格")
    @Excel(name = "价格")
    private Double price;

    @ApiModelProperty(value = "优先值")
    @Excel(name = "优先值")
    private Integer priorityLevel;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否激活")
    @Excel(name = "是否激活")
    private Boolean isActive;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

}
