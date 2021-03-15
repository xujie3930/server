package com.msd.chargerules.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "OperationDTO", description = "OperationDTO对象")
public class OperationDTO implements Serializable {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "首件价格")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件价格")
    private BigDecimal nextPrice;

    @ApiModelProperty(value = "计费单位")
    private String chargeUnit;

    @ApiModelProperty(value = "备注")
    private String remark;



}
