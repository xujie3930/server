package com.szmsd.chargerules.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.exception.com.AssertUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "OperationDTO", description = "OperationDTO对象")
public class OperationQueryDTO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "仓库", required = true)
    private String warehouseCode;
    @ApiModelProperty(value = "操作类型", required = true)
    private String operationType;
    @ApiModelProperty(value = "操作类型名称", required = true)
    private String operationTypeName;
    @ApiModelProperty(value = "订单类型", required = true)
    private String orderType;
    @ApiModelProperty(value = "币种编码", required = true)
    private String currencyCode;
    @ApiModelProperty(value = "币种名称", required = true)
    private String currencyName;
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

    @ApiModelProperty(value = "重量", hidden = true)
    private Double weight;

    public OperationQueryDTO(String operationType, String orderType, String warehouseCode, Double weight) {
        this.operationType = operationType;
        this.orderType = orderType;
        this.warehouseCode = warehouseCode;
        this.weight = weight;
    }
}
