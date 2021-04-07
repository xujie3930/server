package com.szmsd.chargerules.domain;

import com.baomidou.mybatisplus.annotation.*;
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
@ApiModel(value = "ChargeLog", description = "扣费日志")
@TableName("cha_log")
@AllArgsConstructor
@NoArgsConstructor
public class ChargeLog extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    @ApiModelProperty(value = "信息")
    private String message;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    public ChargeLog(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public ChargeLog(String orderNo, String operationType, String warehouseCode) {
        this.operationType = operationType;
        this.orderNo = orderNo;
        this.warehouseCode = warehouseCode;
    }
}
