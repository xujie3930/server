package com.szmsd.chargerules.vo;

import com.szmsd.common.plugin.annotation.AutoFieldI18n;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "FreightCalculationVO", description = "运费测算列表")
public class FreightCalculationVO {

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "计费重量")
    private BigDecimal calcWeight;

    @AutoFieldI18n
    @ApiModelProperty("计费重类型")
    private String calcType;

    @ApiModelProperty("预估总费用")
    private String totalAmount;

    @ApiModelProperty("费用明细")
    private String amountDetail;

    @ApiModelProperty("预估时效")
    private String timeliness;

}
