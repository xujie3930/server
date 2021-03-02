package com.szmsd.bas.api.domain;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author chenanze
 * @date 2020-09-16
 * @description
 */
@Data
@Accessors(chain = true)
public class Cus {

    @ApiModelProperty(value = "费用类别（运费 ，纯运费）")
    @Excel(name = "费用类别（运费 ，纯运费）")
    private String costCategory;

    @ApiModelProperty(value = "产品类型id")
    @Excel(name = "产品类型id")
    private String productTypeCode;

    @ApiModelProperty(value = "客户id")
    @Excel(name = "客户id")
    private String cusCode;

    @ApiModelProperty(value = "重量")
    @Excel(name = "重量")
    private String heft;

    @ApiModelProperty(value = "目的地")
    private String destinationCode;
}
