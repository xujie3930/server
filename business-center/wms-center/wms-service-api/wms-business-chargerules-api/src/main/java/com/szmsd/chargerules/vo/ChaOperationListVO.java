package com.szmsd.chargerules.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "OperationVO对象")
public class ChaOperationListVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;
    @NotBlank
    @ApiModelProperty(value = "仓库", required = true)
    private String warehouseCode;
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;
    @NotBlank
    @ApiModelProperty(value = "操作类型", required = true)
    private String operationType;
    @NotBlank
    @ApiModelProperty(value = "操作类型名称", required = true)
    private String operationTypeName;
    @NotBlank
    @ApiModelProperty(value = "订单类型", required = true)
    private String orderType;
    @NotBlank
    @ApiModelProperty(value = "币种编码", required = true)
    private String currencyCode;
    @NotBlank
    @ApiModelProperty(value = "币种名称", required = true)
    private String currencyName;
    @NotBlank
    @ApiModelProperty(value = "客户类型编码", required = true)
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

    @ApiModelProperty(value = "备注")
    private String remark;

}
