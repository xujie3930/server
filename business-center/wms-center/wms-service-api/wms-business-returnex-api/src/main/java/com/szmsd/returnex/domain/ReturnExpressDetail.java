package com.szmsd.returnex.domain;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.returnex.config.BOConvert;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @ClassName: ReturnExpressDetail
 * @Description: 退货
 * @Author: 11
 * @Date: 2021/3/26 11:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "return_express - 退货单详情表", description = "ReturnExpressDetail对象")
public class ReturnExpressDetail extends BaseEntity implements BOConvert {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private BigDecimal version;

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

    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "WMS处理单号")
    @Excel(name = "WMS处理单号")
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

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic(delval = "2", value = "0")
    private Integer delFlag;
    /**---ADD----*/
    @ApiModelProperty(value = "过期时间")
    @Excel(name = "过期时间")
    private Date expireTime;

    @ApiModelProperty(value = "处理时间")
    @Excel(name = "处理时间")
    private Date processTime;

    @ApiModelProperty(value = "客户备注")
    @Excel(name = "客户备注")
    private String customerRemark;

    @ApiModelProperty(value = "新出库单号")
    @Excel(name = "新出库单号")
    private String fromOrderNoNew;

    @ApiModelProperty(value = "新物流跟踪号")
    @Excel(name = "新物流跟踪号")
    private String scanCodeNew;

    @ApiModelProperty(value = "refNo")
    private String refNo;


    @ApiModelProperty(value = "国家代码")
    private String countryCode;


    @ApiModelProperty(value = "国家名称")
    private String country;


    @ApiModelProperty(value = "到期时长")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer expirationDuration;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
