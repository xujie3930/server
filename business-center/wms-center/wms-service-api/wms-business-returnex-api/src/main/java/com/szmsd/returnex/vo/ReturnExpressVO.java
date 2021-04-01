package com.szmsd.returnex.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.enums.LanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
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
@ApiModel(value = "ReturnExpressVO", description = "退货单详情VO")
public class ReturnExpressVO {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "退件原始单号 原出库单号")
    @Excel(name = "退件原始单号 原出库单号")
    private String fromOrderNo;

    @ApiModelProperty(value = "退件可扫描编码")
    @Excel(name = "退件可扫描编码")
    private String scanCode;

    @ApiModelProperty(value = "预报单号")
    @Excel(name = "预报单号")
    private String expectedNo;

    @ApiModelProperty(value = "VMS处理单号")
    @Excel(name = "VMS处理单号")
    private String returnNo;

    @ApiModelProperty(value = "申请处理方式")
    @Excel(name = "申请处理方式")
    private String processType;
    @ApiModelProperty(value = "申请处理方式")
    @Excel(name = "申请处理方式")
    private String processTypeStr;

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String returnType;
    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String returnTypeStr;

    @ApiModelProperty(value = "退件目标仓库编码")
    @Excel(name = "退件目标仓库编码")
    private String warehouseCode;
    @ApiModelProperty(value = "退件目标仓库编码")
    @Excel(name = "退件目标仓库编码")
    private String warehouseCodeStr;

    @ApiModelProperty(value = "退货渠道", example = "客户自选")
    @Excel(name = "退货渠道")
    private String returnChannel;

    @ApiModelProperty(value = "申请处理方式编码")
    @Excel(name = "申请处理方式编码")
    private String applyProcessMethod;
    @ApiModelProperty(value = "申请处理方式编码")
    @Excel(name = "申请处理方式编码")
    private String applyProcessMethodStr;

    @ApiModelProperty(value = "到仓时间")
    @Excel(name = "到仓时间")
    private LocalDateTime arrivalTime;

    @ApiModelProperty(value = "完成时间")
    @Excel(name = "完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "是否逾期")
    @Excel(name = "是否逾期")
    private String overdue;

    @ApiModelProperty(value = "处理备注")
    @Excel(name = "处理备注")
    private String processRemark;

    @ApiModelProperty(value = "退件单来源[默认：1：申请退件]")
    @Excel(name = "退件单来源[默认：1：申请退件]")
    private String returnSource;

    @ApiModelProperty(value = "退件单来源[默认：1：申请退件]")
    @Excel(name = "退件单来源[默认：1：申请退件]")
    private String returnSourceStr;

    @ApiModelProperty(value = "处理状态编码")
    @Excel(name = "处理状态编码")
    private String dealStatus;
    @ApiModelProperty(value = "处理状态编码")
    @Excel(name = "处理状态编码")
    private String dealStatusStr;

}
