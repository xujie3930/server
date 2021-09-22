package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName: RefundRequestQueryDTO
 * @Description: 退款申请查询条件
 * @Author: 11
 * @Date: 2021-08-13 11:46
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "退款申请新增修改对象")
public class RefundRequestImportDTO {

    @ApiModelProperty(value = "id")
    private Integer id;

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

    @ApiModelProperty(value = "标准赔付")
    @Excel(name = "标准赔付")
    private String standardPayout;

    @ApiModelProperty(value = "额外赔付")
    @Excel(name = "额外赔付")
    private String additionalPayout;

    @ApiModelProperty(value = "赔付币别")
    @Excel(name = "赔付币别")
    private String compensationPaymentCurrency;

    @ApiModelProperty(value = "赔付币别编码")
    @Excel(name = "赔付币别编码")
    private String compensationPaymentCurrencyCode;

    @ApiModelProperty(value = "供应商是否完成赔付（0：未完成，1：已完成）")
    @Excel(name = "供应商是否完成赔付（0：未完成，1：已完成）")
    private Boolean compensationPaymentFlag;

    @ApiModelProperty(value = "赔付金额")
    @Excel(name = "赔付金额")
    private String payoutAmount;

    @ApiModelProperty(value = "供应商确认不赔付（0：否，1：是）")
    @Excel(name = "供应商确认不赔付（0：否，1：是）")
    private Boolean noCompensationFlag;

    @ApiModelProperty(value = "供应商确认赔付未到账（0：否，1：是）")
    @Excel(name = "供应商确认赔付未到账（0：否，1：是）")
    private Boolean compensationPaymentArrivedFlag;

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
    private List<AttachmentFileDTO> attachment;

    public String getAttachment() {
        if (CollectionUtils.isEmpty(attachment)) return "";
        return JSONObject.toJSONString(attachment);
    }

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
