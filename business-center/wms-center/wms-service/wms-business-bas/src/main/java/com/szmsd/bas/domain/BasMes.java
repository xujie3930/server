package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-08-20
 */
@TableName("bas_mes")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasMes对象", description = "消息表")
public class BasMes {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "ID", type = IdType.UUID)
    @Excel(name = "主键id")
    private String id;

    @ApiModelProperty(value = "短信流水号")
    @Excel(name = "短信流水号")
    private String messageId;

    @ApiModelProperty(value = "接收手机号")
    @Excel(name = "接收手机号")
    private String iphone;

    @ApiModelProperty(value = "短信内容")
    @Excel(name = "短信内容")
    private String message;

    @ApiModelProperty(value = "公司名称")
    @Excel(name = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "客户编号")
    @Excel(name = "客户编号")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "运单号")
    @Excel(name = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "发送方式")
    @Excel(name = "发送方式")
    private String sendMode;

    @ApiModelProperty(value = "短信类型")
    @Excel(name = "短信类型")
    private String messageType;

    @ApiModelProperty(value = "发送标识")
    @Excel(name = "发送标识")
    private String sendIden;

    @ApiModelProperty(value = "短信来源")
    @Excel(name = "短信来源")
    private String messageSource;

    @ApiModelProperty(value = "发送网点")
    @Excel(name = "发送网点")
    private String createSite;

    @ApiModelProperty(value = "发送网点编号")
    @Excel(name = "发送网点编号")
    private String createSiteCode;

    @ApiModelProperty(value = "发送失败原因")
    @Excel(name = "发送失败原因")
    private String sendFail;

    @ApiModelProperty(value = "付费网点")
    @Excel(name = "付费网点")
    private String paySite;

    @ApiModelProperty(value = "付费网点编号")
    @Excel(name = "付费网点编号")
    private String paySiteCode;

    @ApiModelProperty(value = "通道编号")
    @Excel(name = "通道编号")
    private String passagewayCode;

    @ApiModelProperty(value = "发送时间")
    @Excel(name = "发送时间")
    private Date createTime;

    @ApiModelProperty(value = "发送人")
    @Excel(name = "发送人")
    private String createBy;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    @Excel(name = "开始时间")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    @Excel(name = "结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "寄件人手机")
    @Excel(name = "寄件人手机")
    private String senderTel;

    @TableField(exist = false)
    @ApiModelProperty(value = "收件人手机")
    @Excel(name = "收件人手机")
    private String receiverTel;

    @TableField(exist = false)
    @ApiModelProperty(value = "代收货款金额")
    @Excel(name = "代收货款金额")
    private String paymentAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "成功条数")
    @Excel(name = "成功条数")
    private String success;

    @TableField(exist = false)
    @ApiModelProperty(value = "全部条数")
    @Excel(name = "全部条数")
    private String all;

    @ApiModelProperty(value = "短信费用")
    @Excel(name = "短信费用")
    private BigDecimal mesCost;
}
