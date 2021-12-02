package com.szmsd.chargerules.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.chargerules.domain.WarehouseOperationDetails;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
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

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "客户类型编码")
    @Excel(name = "客户类型编码")
    private String cusTypeCode;

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
