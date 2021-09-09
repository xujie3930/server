package com.szmsd.finance.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName: RefundRequestQueryDTO
 * @Description: 退款申请查询条件
 * @Author: 11
 * @Date: 2021-08-13 11:46
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "退款申请详情")
public class RefundRequestVO {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "处理编号")
    @Excel(name = "处理编号")
    private String processNo;

    @ApiModelProperty(value = "状态[0：待审核,1：拒绝,2：审核通过]")
    @Excel(name = "状态",readConverterExp = "0=待审核,1=拒绝,2=审核通过")
    private Integer auditStatus;
    @ApiModelProperty(value = "申请时间")
    @Excel(name = "申请时间")
    private Date createTime;

    @ApiModelProperty(value = "处理号（工单id)")
    @Excel(name = "处理号")
    private String orderNo;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String cusCode;

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

    @ApiModelProperty(value = "所属仓库")
    @Excel(name = "所属仓库")
    private String warehouseName;

    @ApiModelProperty(value = "所属仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "业务类型")
    @Excel(name = "业务类型")
    private String businessTypeName;

    @ApiModelProperty(value = "业务类型编码")
    private String businessTypeCode;

    @ApiModelProperty(value = "业务明细")
    @Excel(name = "业务明细")
    private String businessDetails;

    @ApiModelProperty(value = "费用类型")
    @Excel(name = "费用类型")
    private String feeTypeName;

    @ApiModelProperty(value = "费用类型编码")
    private String feeTypeCode;

    @ApiModelProperty(value = "费用类别")
    @Excel(name = "费用类别")
    private String feeCategoryName;

    @ApiModelProperty(value = "费用类别编码")
    private String feeCategoryCode;

    @ApiModelProperty(value = "附注")
    private String remark;

    @ApiModelProperty(value = "金额")
    @Excel(name = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种")
    @Excel(name = "币种")
    private String currencyName;

    @ApiModelProperty(value = "属性-数组")
    @Excel(name = "属性")
    private String attributes;

    @ApiModelProperty(value = "属性编码-数组")
    private String attributesCode;

    @ApiModelProperty(value = "申请人")
    private String createByName;

    @ApiModelProperty(value = "处理时间")
    @Excel(name = "处理时间")
    private Date auditTime;

    @ApiModelProperty(value = "审核人id")
    private Integer reviewerId;

    @ApiModelProperty(value = "审核人代码")
    private String reviewerCode;

    @ApiModelProperty(value = "操作人")
    @Excel(name = "操作人")
    private String reviewerName;

    @ApiModelProperty(value = "客户id")
    private Integer cusId;

    @ApiModelProperty(value = "客户名称")
    private String cusName;


    @ApiModelProperty(value = "标准赔付")
    @Excel(name = "标准赔付")
    private String standardPayout;

    @ApiModelProperty(value = "额外赔付")
    private String additionalPayout;

    @ApiModelProperty(value = "赔付币别")
    private String compensationPaymentCurrency;

    @ApiModelProperty(value = "赔付币别编码")
    private String compensationPaymentCurrencyCode;

    @ApiModelProperty(value = "供应商是否完成赔付（0：未完成，1：已完成）")
    private Boolean compensationPaymentFlag;

    @ApiModelProperty(value = "赔付金额")
    private String payoutAmount;

    @ApiModelProperty(value = "供应商确认不赔付（0：否，1：是）")
    private Boolean noCompensationFlag;

    @ApiModelProperty(value = "供应商确认赔付未到账（0：否，1：是）")
    private Boolean compensationPaymentArrivedFlag;

    @ApiModelProperty(value = "数量")
    private String num;

    @ApiModelProperty(value = "处理号类型")
    private String orderType;

    @ApiModelProperty(value = "附件")
    private List<AttachmentFileDTO> attachment;

    public void setAttachment(String attachment) {
        Optional.ofNullable(attachment).filter(StringUtils::isNotBlank).ifPresent(x -> {
            this.attachment = JSONObject.parseArray(x, AttachmentFileDTO.class);
        });
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
