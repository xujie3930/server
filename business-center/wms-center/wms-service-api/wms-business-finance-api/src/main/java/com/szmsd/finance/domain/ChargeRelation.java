package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "费用类型关系表", description = "费用类型关系表")
@TableName("fss_charge_relation")
public class ChargeRelation implements Serializable {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "性质")
    private String nature;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "单据类型")
    private String orderType;

    @ApiModelProperty(value = "费用类别转换后")
    private String chargeCategoryChange;

    @ApiModelProperty(value = "费用类别(第三方名称)")
    private String chargeCategory;


}
