package com.szmsd.http.dto.custom;

import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricesDiscountDetailDto", description = "客户方案-折扣明细")
public class DiscountDetailFormulaDto {

    private int startPrice;

    private int deltaWeightPerStage;

    private int deltaChargePerStage;

    private int detalNumberPerQuantity;

    private int detalChargePerQuantity;

    private int minPrice;

    private int maxPrice;

    private int percentage;

    private String chargeRuleType;

}
