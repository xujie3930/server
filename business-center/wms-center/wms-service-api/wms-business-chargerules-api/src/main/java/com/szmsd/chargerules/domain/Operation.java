package com.szmsd.chargerules.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "业务操作", description = "业务操作表")
@TableName("cha_operation")
public class Operation extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作类型")
    @TableField
    private String operationType;

    @ApiModelProperty(value = "操作类型名称")
    @TableField(exist = false)
    private String operationTypeName;

    @ApiModelProperty(value = "最小重量 - 开始 单位: g 大于")
    @TableField
    private Double minimumWeight;

    @ApiModelProperty(value = "最大重量 - 结束 单位: g 小于等于")
    @TableField
    private Double maximumWeight;

    @ApiModelProperty(value = "仓库")
    @TableField
    private String warehouseCode;

    @TableField(exist = false)
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "订单类型")
    @TableField
    private String orderType;

    @TableField(exist = false)
    @ApiModelProperty(value = "订单类型名称")
    private String orderTypeName;

    @ApiModelProperty(value = "首件价格")
    @TableField
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件价格")
    @TableField
    private BigDecimal nextPrice;

    @ApiModelProperty(value = "计费单位")
    @TableField
    private String unit;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    public String getWarehouseName() {
        return warehouseCode;
    }

    public String getOperationTypeName() {
        DelOutboundOrderTypeEnum delOutboundOrderTypeEnum = DelOutboundOrderTypeEnum.get(this.operationType);
        if (delOutboundOrderTypeEnum != null) {
            return delOutboundOrderTypeEnum.getName();
        }
        return null;
    }

    public String getOrderTypeName() {
        return OrderTypeEnum.get(this.orderType);
    }

}
