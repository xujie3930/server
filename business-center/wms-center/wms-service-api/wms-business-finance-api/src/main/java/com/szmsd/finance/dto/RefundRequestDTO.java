package com.szmsd.finance.dto;

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

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;
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
public class RefundRequestDTO {

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

    @ApiModelProperty(value = "所属仓库")
    @Excel(name = "所属仓库")
    private String warehouseName;

    @ApiModelProperty(value = "所属仓库编码")
    @Excel(name = "所属仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "产品类型")
    @Excel(name = "产品类型")
    private String productTypeName;

    @ApiModelProperty(value = "产品类型编码")
    @Excel(name = "产品类型编码")
    private String productTypeCode;

    @NotBlank(message = "单号不能为空")
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
