package com.szmsd.chargerules.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.bas.plugin.BasSubCodeCommonParameter;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.chargerules.domain.WarehouseOperationDetails;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import com.szmsd.common.plugin.interfaces.DefaultCommonParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "WarehouseOperationVo", description = "仓储业务操作")
public class WarehouseOperationVo {

    @ApiModelProperty(value = "id")
    private Integer id;

    @AutoFieldValue(supports = "BasWarehouse", cp = DefaultCommonParameter.class, nameField = "warehouseName")
    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, nameField = "currencyName", code = "008", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, code = "098", nameField = "cusTypeCodeName", cp = BasSubCodeCommonParameter.class)
    @ApiModelProperty(value = "客户类型编码")
    @Excel(name = "客户类型编码")
    private String cusTypeCode;

    @ApiModelProperty(value = "客户类型编码")
    @Excel(name = "客户类型编码")
    private String cusTypeCodeName;

    @ApiModelProperty(value = "客户名称 A,B")
    @Excel(name = "客户名称 A,B")
    private String cusNameList;

    @ApiModelProperty(value = "客户编码 CNI1,CNI2")
    @Excel(name = "客户编码 CNI1,CNI2")
    private String cusCodeList;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生效时间")
    @Excel(name = "生效时间")
    private LocalDateTime effectiveTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "失效时间")
    @Excel(name = "失效时间")
    private LocalDateTime expirationTime;

    @ApiModelProperty(value = "详情")
    private List<WarehouseOperationDetails> details;

    public String getWarehouseName() {
        return warehouseCode;
    }

}
