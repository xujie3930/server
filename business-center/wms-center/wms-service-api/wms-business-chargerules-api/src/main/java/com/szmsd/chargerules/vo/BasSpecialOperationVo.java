package com.szmsd.chargerules.vo;

import com.szmsd.chargerules.domain.SpecialOperation;
import com.szmsd.chargerules.enums.SpecialOperationStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasSpecialOperationDTO", description = "特殊操作")
public class BasSpecialOperationVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

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

    @ApiModelProperty(value = "oms备注")
    private String omsRemark;

    @ApiModelProperty(value = "状态（审核结果）")
    private String status;

    @ApiModelProperty(value = "list")
    private SpecialOperation specialOperationList;

}
