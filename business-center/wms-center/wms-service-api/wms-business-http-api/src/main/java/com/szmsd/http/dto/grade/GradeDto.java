package com.szmsd.http.dto.grade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "GradeDto", description = "客户方案-修改等级")
public class GradeDto {


    @ApiModelProperty("产品代码")
    private String productCode;

    @ApiModelProperty("等级")
    private String grade;

    @ApiModelProperty("子产品代码")
    private String subProduct;

    @ApiModelProperty("仓库代码（处理点）")
    private String warehouseCode;

    @ApiModelProperty("仓库代码（处理点）集合")
    private List<String> warehouseCodeList;

    @ApiModelProperty("国家")
    private String defaultCountry;

    @ApiModelProperty("有效起始时间")
    private String beginTime;

    @ApiModelProperty("有效结束时间 若时间为DateTime.MaxValue 表示无结束时间")
    private String endTime;

    @ApiModelProperty("产品类别")
    private String pricingProductCategory;

}
