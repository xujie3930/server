package com.szmsd.chargerules.vo;

import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "SpecialOperationVo", description = "特殊操作")
public class SpecialOperationVo {

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "首件价格")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件价格")
    private BigDecimal nextPrice;

    @ApiModelProperty(value = "计费单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String remark;

    public String getWarehouseName() {
        return warehouseCode;
    }

}
