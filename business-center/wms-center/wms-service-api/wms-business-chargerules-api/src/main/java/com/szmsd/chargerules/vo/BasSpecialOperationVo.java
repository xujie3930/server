package com.szmsd.chargerules.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasSpecialOperationVo", description = "特殊操作详情")
public class BasSpecialOperationVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "客户编号")
    private String customCode;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @TableField(exist = false)
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    private String transactionId;

    @ApiModelProperty(value = "操作单号")
    private String operationOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "数量")
    private Integer qty;

    @ApiModelProperty(value = "系数")
    private Integer coefficient;

    @ApiModelProperty(value = "计费单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "oms备注")
    private String omsRemark;

    @ApiModelProperty(value = "状态（审核结果）通过：1、不通过：2、待审批：3")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "不通过原因")
    private String reason;

    @ApiModelProperty(value = "首件价格")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件价格")
    private BigDecimal nextPrice;

    public String getWarehouseName() {
        return warehouseCode;
    }

    public String getOrderType() {
        return OrderTypeEnum.get(orderType);
    }

}
