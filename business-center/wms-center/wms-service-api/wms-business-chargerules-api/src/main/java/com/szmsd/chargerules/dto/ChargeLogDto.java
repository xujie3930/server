package com.szmsd.chargerules.dto;

import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "ChargeLog", description = "日志查询")
@AllArgsConstructor
@NoArgsConstructor
public class ChargeLogDto extends BaseEntity {

    @ApiModelProperty(value = "客户编号")
    private String customCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyCode;

    @ApiModelProperty(value = "交易类型")
    private String payMethod;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    public ChargeLogDto(String orderNo, String payMethod, String operationType, Boolean success) {
        this.orderNo = orderNo;
        this.payMethod = payMethod;
        this.operationType = operationType;
        this.success = success;
    }
}
