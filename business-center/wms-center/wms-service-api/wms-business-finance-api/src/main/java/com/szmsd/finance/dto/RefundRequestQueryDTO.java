package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.utils.StringToolkit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

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
@ApiModel(description = "退款申请查询条件")
public class RefundRequestQueryDTO {

    @ApiModelProperty(value = "申请时间-开始")
    private Date createTimeStart;

    @ApiModelProperty(value = "申请时间-结束")
    private Date createTimeEnd;

    @ApiModelProperty(value = "处理编号")
    private String processNo;

    public void setProcessNo(String processNo) {
        this.processNo = processNo;
        this.processNoList = StringToolkit.getCodeByArray(processNo);
    }

    @ApiModelProperty(value = "处理编号", hidden = true)
    private List<String> processNoList;

    @ApiModelProperty(value = "处理号（工单id)")
    private String orderNo;

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        this.orderNoList = StringToolkit.getCodeByArray(orderNo);
    }

    @ApiModelProperty(value = "处理号（工单id)", hidden = true)
    private List<String> orderNoList;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "申请人")
    private String createByName;

    @ApiModelProperty(value = "属性")
    private String attributes;

    @ApiModelProperty(value = "处理时间")
    private Date auditTimeStart;

    @ApiModelProperty(value = "处理时间")
    private Date auditTimeEnd;

    @ApiModelProperty(value = "业务类型")
    private String businessTypeName;

    @ApiModelProperty(value = "业务类型编码")
    private String businessTypeCode;

    @ApiModelProperty(value = "责任地区")
    private String responsibilityArea;

    @ApiModelProperty(value = "责任地区编码")
    private String responsibilityAreaCode;

    @ApiModelProperty(value = "处理性质")
    private String treatmentProperties;

    @ApiModelProperty(value = "处理性质编码")
    private String treatmentPropertiesCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
