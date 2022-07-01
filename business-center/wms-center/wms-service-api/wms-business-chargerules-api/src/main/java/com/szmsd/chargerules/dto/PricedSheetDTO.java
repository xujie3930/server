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

    @ApiModelProperty(value = "等级编码")
    private String gradeCode;

    @ApiModelProperty(value = "等级名称")
    private String gradeName;

}
