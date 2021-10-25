package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PricedProductSearchCriteria extends PageDTO {

    @ApiModelProperty(value = "产品代码")
    private String code;

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "产品类别--直发(DirectExpress),二程(Outbound)")
    private String category;

    @ApiModelProperty(value = "产品服务")
    private String service;

    @ApiModelProperty(value = "产品类型--普通产品类型(Basic),组合产品类型(Group)")
    private String type;

    @ApiModelProperty(value = "是否可查询")
    private Boolean isShow;

    @ApiModelProperty(value = "是否可下单")
    private String inService;

    @ApiModelProperty(value = "当前页码", example = "1")
    private Integer pageNumber;

    @ApiModelProperty(value = "页面大小", example = "10")
    private Integer pageSize;
}
