package com.szmsd.http.vo;

import com.szmsd.http.dto.Weight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "WeightAddition", description = "重量加成配置")
public class WeightAddition {

    @ApiModelProperty(value = "需要重量加成的包裹类型")
    private String packagesType;

    @ApiModelProperty(value = "重量加成百分比")
    private BigDecimal additionPercentage;

    @ApiModelProperty(value = "Weight")
    private Weight maxAdditionWeight;

}
