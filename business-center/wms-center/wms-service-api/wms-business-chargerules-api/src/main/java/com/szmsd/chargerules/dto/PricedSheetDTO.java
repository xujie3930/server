package com.szmsd.chargerules.dto;

import com.szmsd.chargerules.vo.PricedSheetInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PricedSheetDTO extends PricedSheetInfoVO {

    @ApiModelProperty("产品代码")
    private String productCode;

    @ApiModelProperty(value = "等级")
    private String grade;

    @ApiModelProperty("生效开始时间")
    private String effectiveStartTime;

    @ApiModelProperty("生效结束时间")
    private String effectiveEndTime;

}
