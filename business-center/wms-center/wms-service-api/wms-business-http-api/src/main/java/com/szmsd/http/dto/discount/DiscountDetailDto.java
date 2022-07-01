package com.szmsd.http.dto.discount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "DiscountDetailDto", description = "折扣方案-关联产品")
public class DiscountDetailDto {



    @ApiModelProperty("折扣信息Id")
    private String id;

    @ApiModelProperty("产品代码")
    private String productCode;

    @ApiModelProperty("分区")
    private String zone;

    @ApiModelProperty("重量单位")
    private String weightUnit;

    @ApiModelProperty("包裹限制条件")
    private DiscountDetailPackageLimitDto packageLimit;

    @ApiModelProperty("处理点")
    private String warehourse;

    @ApiModelProperty("偏远地区附加费模板")
    private String remoteModel;

    @ApiModelProperty("费用类型")
    private String chargeType;

    @ApiModelProperty("费用计算公式")
    private DiscountDetailFormulaDto formula;

    @ApiModelProperty("有效起始时间")
    private String beginTime;


    @ApiModelProperty("有效结束时间 若时间为DateTime.MaxValue 表示无结束时间")
    private String endTime;

}
