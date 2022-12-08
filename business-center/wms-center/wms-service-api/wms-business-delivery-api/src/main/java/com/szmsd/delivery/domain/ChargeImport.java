package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@TableName(value = "charge_import")
@ApiModel(value = "二次计费导入", description = "二次计费导入")
public class ChargeImport extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "DM单号")
    private String orderNo;

    @ApiModelProperty(value = "实重")
    private BigDecimal weight;

    @ApiModelProperty(value = "长")
    private BigDecimal length;

    @ApiModelProperty(value = "宽")
    private BigDecimal width;

    @ApiModelProperty(value = "高")
    private BigDecimal height;

    @ApiModelProperty(value = "计费重")
    private BigDecimal calcWeight;

    @ApiModelProperty(value = "重量单位")
    private String weightUnit;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "报价表编号")
    private String quotationNo;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    @ApiModelProperty(value = "删除状态 1 已删除")
    private Integer delFlag;
}
