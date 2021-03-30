package com.szmsd.http.vo;

import com.szmsd.http.dto.Weight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "DirectServiceFeeData")
public class DirectServiceFeeData {

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "发货渠道")
    private String shippingChannel;

    @ApiModelProperty(value = "时效最小值")
    private Integer timelinessMin;

    @ApiModelProperty(value = "时效最大值")
    private Integer timelinessMax;

    @ApiModelProperty(value = "金额记录")
    private ChargeModel chargeItems;

    @ApiModelProperty
    private List<String> tags;

    @ApiModelProperty
    private Weight weight;

    @ApiModelProperty
    private Weight volumeWeight;

    @ApiModelProperty
    private Weight calcWeight;

    @ApiModelProperty
    private String calcType;


}
