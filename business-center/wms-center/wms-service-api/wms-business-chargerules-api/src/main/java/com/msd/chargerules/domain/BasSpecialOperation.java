package com.msd.chargerules.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "BasSpecialOperationDTO", description = "特殊操作")
@TableName("bas_special_operation")
public class BasSpecialOperation extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    private String transactionId;

    @NotBlank(message = "操作单号不能为空")
    @ApiModelProperty(value = "操作单号")
    private String operationOrderNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @NotBlank(message = "操作类型不能为空")
    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @NotNull(message = "数量不能为空")
    @ApiModelProperty(value = "数量")
    private Integer qty;

    @Min(0)
    @NotNull(message = "系数不能为空")
    @ApiModelProperty(value = "系数")
    private Integer coefficient;

    @ApiModelProperty(value = "状态（审核结果）")
    private String status;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

}
