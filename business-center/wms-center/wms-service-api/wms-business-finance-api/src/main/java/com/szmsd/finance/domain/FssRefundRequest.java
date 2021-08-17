package com.szmsd.finance.domain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.web.domain.BaseEntity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
 * <p>
 * 退费记录表
 * </p>
 *
 * @author 11
 * @since 2021-08-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "退费记录表", description = "FssRefundRequest对象")
public class FssRefundRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "审核状态[0：待审核,1：拒绝,2：审核通过]")
    @Excel(name = "审核状态[0：待审核,1：拒绝,2：审核通过]")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核时间")
    @Excel(name = "审核时间")
    private Date auditTime;

    @ApiModelProperty(value = "审核人id")
    @Excel(name = "审核人id")
    private Integer reviewerId;

    @ApiModelProperty(value = "审核人代码")
    @Excel(name = "审核人代码")
    private String reviewerCode;

    @ApiModelProperty(value = "审核人名称")
    @Excel(name = "审核人名称")
    private String reviewerName;

    @ApiModelProperty(value = "客户id")
    @Excel(name = "客户id")
    private Integer cusId;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "处理性质")
    @Excel(name = "处理性质")
    private String treatmentProperties;

    @ApiModelProperty(value = "处理性质编码")
    @Excel(name = "处理性质编码")
    private String treatmentPropertiesCode;

    @ApiModelProperty(value = "责任地区")
    @Excel(name = "责任地区")
    private String responsibilityArea;

    @ApiModelProperty(value = "责任地区编码")
    @Excel(name = "责任地区编码")
    private String responsibilityAreaCode;

    @ApiModelProperty(value = "所属仓库")
    @Excel(name = "所属仓库")
    private String warehouseName;

    @ApiModelProperty(value = "所属仓库编码")
    @Excel(name = "所属仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "供应商是否完成赔付（0：未完成，1：已完成）")
    @Excel(name = "供应商是否完成赔付（0：未完成，1：已完成）")
    private Boolean compensationPaymentFlag;

    @ApiModelProperty(value = "供应商确认不赔付（0：否，1：是）")
    @Excel(name = "供应商确认不赔付（0：否，1：是）")
    private Boolean noCompensationFlag;

    @ApiModelProperty(value = "供应商确认赔付未到账（0：否，1：是）")
    @Excel(name = "供应商确认赔付未到账（0：否，1：是）")
    private Boolean compensationPaymentArrivedFlag;

    @ApiModelProperty(value = "产品类型")
    @Excel(name = "产品类型")
    private String productTypeName;

    @ApiModelProperty(value = "产品类型编码")
    @Excel(name = "产品类型编码")
    private String productTypeCode;

    @ApiModelProperty(value = "单号")
    @Excel(name = "单号")
    private String orderNo;

    @ApiModelProperty(value = "单号类型")
    @Excel(name = "单号类型")
    private String orderType;

    @ApiModelProperty(value = "币种编码")
    @Excel(name = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    @Excel(name = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "金额")
    @Excel(name = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "赔付币别")
    @Excel(name = "赔付币别")
    private String compensationPaymentCurrency;

    @ApiModelProperty(value = "赔付币别编码")
    @Excel(name = "赔付币别编码")
    private String compensationPaymentCurrencyCode;

    @ApiModelProperty(value = "赔付金额")
    @Excel(name = "赔付金额")
    private BigDecimal compensationPaymentAmount;

    @ApiModelProperty(value = "数量")
    @Excel(name = "数量")
    private Integer num;

    @ApiModelProperty(value = "属性-数组")
    @Excel(name = "属性-数组")
    private String attributes;

    @ApiModelProperty(value = "属性编码-数组")
    @Excel(name = "属性编码-数组")
    private String attributesCode;

    @ApiModelProperty(value = "附件")
    @Excel(name = "附件")
    private String attachment;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;


}
