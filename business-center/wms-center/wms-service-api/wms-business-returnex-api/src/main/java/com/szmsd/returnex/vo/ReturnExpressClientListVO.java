package com.szmsd.returnex.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.bas.plugin.BasSubCodeCommonParameter;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ClassName: ReturnExpressVO
 * @Description: 退款单列表对象
 * @Author: 11
 * @Date: 2021/3/26 13:41
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "退货单列表详情VO")
public class ReturnExpressClientListVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Excel(name = "预报单号")
    @ApiModelProperty(value = "预报单号")
    private String expectedNo;
    @Excel(name = "WMS处理单号")
    @ApiModelProperty(value = "WMS处理单号 退件单号")
    private String returnNo;
    @Excel(name = "出库单号")
    @ApiModelProperty(value = "出库单号")
    private String fromOrderNo;
    @Excel(name = "原跟踪号")
    @ApiModelProperty(value = "退件可扫描编码 原跟踪号")
    private String scanCode;
    @Excel(name = "类型")
    @ApiModelProperty(value = "退件单来源[默认：1：退件预报2：VMS通知退件]", hidden = true)
    private String returnSourceStr;
    @Excel(name = "退件目标仓库")
    @ApiModelProperty(value = "目的仓库名称", hidden = true)
    private String warehouseCodeStr;
    @Excel(name = "退件类型")
    @ApiModelProperty(value = "退件类型", hidden = true)
    private String returnTypeStr;
    @Excel(name = "申请处理方式")
    @ApiModelProperty(value = "申请处理方式", hidden = true)
    private String processTypeStr;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "到仓时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "到仓时间")
    private Date arrivalTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完成时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;
    @Excel(name = "退回原因")
    @ApiModelProperty(value = "备注(退回原因)")
    private String processRemark;
    @ApiModelProperty(value = "新出库单号 改发处理号")
    @Excel(name = "改发处理号")
    private String fromOrderNoNew;
    @ApiModelProperty(value = "新物流跟踪号 改发跟踪号")
    @Excel(name = "改发跟踪号")
    private String scanCodeNew;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    @Excel(name = "处理时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "过期时间")
    @Excel(name = "过期时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;
    @Excel(name = "是否逾期", readConverterExp = "0=否,1=是")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.YN)
    @ApiModelProperty(value = "是否逾期", hidden = true)
    private String overdueStr;
    @Excel(name = "状态")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.RETURN_EXPRESS)
    @ApiModelProperty(value = "处理状态编码 订单状态", hidden = true)
    private String dealStatusStr;
    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
