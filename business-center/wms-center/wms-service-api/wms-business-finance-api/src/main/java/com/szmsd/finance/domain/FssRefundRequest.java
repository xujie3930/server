package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


/**
 * <p>
 * 退费记录表
 * </p>
 *
 * @author 11
 * @since 2021-08-31
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

    @ApiModelProperty(value = "审核状态：初始、提审、异常、完成")
    @Excel(name = "审核状态", readConverterExp = "0=初始,1=提审,2=异常,3=完成")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核时间")
    @Excel(name = "审核时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
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
    private String treatmentPropertiesCode;

    @ApiModelProperty(value = "责任地区")
    @Excel(name = "责任地区")
    private String responsibilityArea;

    @ApiModelProperty(value = "责任地区编码")
    private String responsibilityAreaCode;

    @ApiModelProperty(value = "标准赔付")
    @Excel(name = "标准赔付")
    private BigDecimal standardPayout;

    @ApiModelProperty(value = "额外赔付")
    @Excel(name = "额外赔付")
    private BigDecimal additionalPayout;

    @ApiModelProperty(value = "赔付币别")
    @Excel(name = "赔付币别")
    private String compensationPaymentCurrency;

    @ApiModelProperty(value = "赔付币别编码")
    @Excel(name = "赔付币别编码")
    private String compensationPaymentCurrencyCode;

    @ApiModelProperty(value = "供应商是否完成赔付（0：未完成，1：已完成）")
    @Excel(name = "供应商是否完成赔付（0：未完成，1：已完成）")
    private String compensationPaymentFlag;

    @ApiModelProperty(value = "赔付金额")
    @Excel(name = "赔付金额")
    private BigDecimal payoutAmount;

    @ApiModelProperty(value = "供应商确认不赔付（0：否，1：是）")
    @Excel(name = "供应商确认不赔付（0：否，1：是）")
    private String noCompensationFlag;

    @ApiModelProperty(value = "供应商确认赔付未到账（0：否，1：是）")
    @Excel(name = "供应商确认赔付未到账（0：否，1：是）")
    private String compensationPaymentArrivedFlag;

    @ApiModelProperty(value = "所属仓库")
    @Excel(name = "所属仓库")
    private String warehouseName;

    @ApiModelProperty(value = "所属仓库编码")
    @Excel(name = "所属仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "业务类型")
    @Excel(name = "业务类型")
    private String businessTypeName;

    @ApiModelProperty(value = "业务类型编码")
    @Excel(name = "业务类型编码")
    private String businessTypeCode;

    @ApiModelProperty(value = "业务明细")
    @Excel(name = "业务明细")
    private String businessDetails;
    @ApiModelProperty(value = "业务明细编码")
    @Excel(name = "业务明细编码")
    private String businessDetailsCode;

    @ApiModelProperty(value = "费用类型")
    @Excel(name = "费用类型")
    private String feeTypeName;

    @ApiModelProperty(value = "费用类型编码")
    @Excel(name = "费用类型编码")
    private String feeTypeCode;

    @ApiModelProperty(value = "费用类别")
    @Excel(name = "费用类别")
    private String feeCategoryName;

    @ApiModelProperty(value = "费用类别编码")
    @Excel(name = "费用类别编码")
    private String feeCategoryCode;

    @ApiModelProperty(value = "数量")
    @Excel(name = "数量")
    private String num;

    @ApiModelProperty(value = "处理号（工单id)")
    @Excel(name = "处理号（工单id)")
    private String orderNo;

    @ApiModelProperty(value = "处理编号")
    @Excel(name = "处理编号")
    private String processNo;

    @ApiModelProperty(value = "处理号类型")
    @Excel(name = "处理号类型")
    private String orderType;

    @ApiModelProperty(value = "金额")
    @Excel(name = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种编码")
    @Excel(name = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    @Excel(name = "币种名称")
    private String currencyName;

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

    @ApiModelProperty(value = "审核驳回原因")
    @Excel(name = "审核驳回原因")
    private String reviewRemark;

}
