package com.szmsd.chargerules.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.bas.plugin.BasSubCodeCommonParameter;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.plugin.annotation.AutoFieldI18n;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import com.szmsd.common.plugin.interfaces.DefaultCommonParameter;
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

    @AutoFieldValue(supports = "BasWarehouse", cp = DefaultCommonParameter.class, nameField = "warehouseName")
    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "操作类型名称")
    private String operationTypeName;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    public void setOrderType(String orderType) {
        this.orderType = orderType;
        if (StringUtils.isNotBlank(orderType)) {
            this.orderTypeName = OrderTypeEnum.get(orderType);
        }
    }

    @AutoFieldI18n
    @ApiModelProperty(value = "订单类型")
    private String orderTypeName;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, nameField = "currencyName", code = "008", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyName;

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

    @ApiModelProperty(value = "备注")
    private String remark;

}
