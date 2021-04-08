package com.szmsd.chargerules.vo;

import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "WarehouseOperationVo", description = "仓储业务操作")
public class WarehouseOperationVo {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "计费天数")
    private Integer chargeDays;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "计费单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String remark;

    public String getWarehouseName() {
        return warehouseCode;
    }

}
