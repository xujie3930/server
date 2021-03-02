package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 问题件记录表
 * </p>
 *
 * @author ziling
 * @since 2020-08-24
 */
@TableName("bas_problem")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasProblem对象", description = "问题件记录表	")
public class BasProblem  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "ID", type = IdType.UUID)
    @Excel(name = "id")
    private String id;

    @ApiModelProperty(value = "运单编号")
    @Excel(name = "运单编号")
    private String waybillNo;

    @ApiModelProperty(value = " 问题件原因 ")
    @Excel(name = " 问题件原因 ")
    private String problemReason;

    @ApiModelProperty(value = "处理结果")
    @Excel(name = "处理结果")
    private String processResult;

    @ApiModelProperty(value = " 处理状态")
    @Excel(name = " 处理状态")
    private Integer centralProcessingStatus;

    @ApiModelProperty(value = "登记人名称")
    @Excel(name = "登记人名称")
    private String registerPersonName;

    @ApiModelProperty(value = "登记网点名称")
    @Excel(name = "登记网点名称")
    private String registerSiteName;

    @ApiModelProperty(value = "登记时间")
    @Excel(name = "登记时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registerTime;

    @ApiModelProperty(value = "寄件网点编码")
    @Excel(name = "寄件网点编码")
    private String shippingSiteCode;

    @ApiModelProperty(value = "寄件网点名称")
    @Excel(name = "寄件网点名称")
    private String shippingSiteName;

    @ApiModelProperty(value = "问题类型")
    @Excel(name = "问题类型")
    private String problemType;

    @ApiModelProperty(value = "问题描述")
    @Excel(name = "问题描述")
    private String problemDescription;

    @ApiModelProperty(value = "寄件日期")
    @Excel(name = "寄件日期")
    private String shippingDate;

    @ApiModelProperty(value = "目的地")
    @Excel(name = "目的地")
    private String destination;

    @ApiModelProperty(value = "件数")
    @Excel(name = "件数")
    private Integer pieces;

    @ApiModelProperty(value = "结算重量")
    @Excel(name = "结算重量")
    private BigDecimal settlementWeight;

    @ApiModelProperty(value = "到付款")
    @Excel(name = "到付款")
    private BigDecimal toPayment;

    @ApiModelProperty(value = "收件地址")
    @Excel(name = "收件地址")
    private String receiverAddress;

    @Excel(name = "")
    private String sender;

    @ApiModelProperty(value = "收件人")
    @Excel(name = "收件人")
    private String receiver;

    @ApiModelProperty(value = "处理人编码")
    @Excel(name = "处理人编码")
    private String processingPersonCode;

    @ApiModelProperty(value = "处理人名称")
    @Excel(name = "处理人名称")
    private String processingPersonName;

    @ApiModelProperty(value = "处理时间")
    @Excel(name = "处理时间")
    private String processingTime;

    @ApiModelProperty(value = "处理网点名称")
    @Excel(name = "处理网点名称")
    private String processingSiteName;

    @ApiModelProperty(value = "派件网点名称")
    @Excel(name = "派件网点名称")
    private String dispatchSiteName;

    @ApiModelProperty(value = "已读标记")
    @Excel(name = "已读标记")
    private String readMark;

    @ApiModelProperty(value = "回复标记")
    @Excel(name = "回复标记")
    private String replayMark;

    @ApiModelProperty(value = "班次")
    @Excel(name = "班次")
    private String shift;

    @ApiModelProperty(value = "操作时间")
    @Excel(name = "操作时间")
    private String operatingTime;

    @ApiModelProperty(value = "回复序号")
    @Excel(name = "回复序号")
    private String replySerialNumber;

    @ApiModelProperty(value = "寄件网点编号")
    @Excel(name = "寄件网点编号")
    private String senderSiteNo;

    @ApiModelProperty(value = "问题件编号")
    @Excel(name = "问题件编号")
    private String problemNo;

    @ApiModelProperty(value = "紧急工单标识")
    @Excel(name = "紧急工单标识")
    private Integer emergencyTicketIdentity;

    @ApiModelProperty(value = "登记人编号")
    @Excel(name = "登记人编号")
    private String registerSiteNo;

    @ApiModelProperty(value = "处理人编号")
    @Excel(name = "处理人编号")
    private String processingPersonNo;

    @ApiModelProperty(value = "处理网点编号")
    @Excel(name = "处理网点编号")
    private String processingSiteNo;

    @ApiModelProperty(value = "派件网点编号")
    @Excel(name = "派件网点编号")
    private String dispatchSiteNo;

    @ApiModelProperty(value = "结束标识")
    @Excel(name = "结束标识")
    private String finishIdentity;

    @ApiModelProperty(value = "分类")
    @Excel(name = "分类")
    private String classification;

    @ApiModelProperty(value = "虚假状态")
    @Excel(name = "虚假状态")
    private String falseState;

    @ApiModelProperty(value = "中心处理标识")
    @Excel(name = "中心处理标识")
    private String centralProcessingId;

    @ApiModelProperty(value = "中心处理结果")
    @Excel(name = "中心处理结果")
    private String centerProcessingResults;

    @ApiModelProperty(value = "中心处理人名称")
    @Excel(name = "中心处理人名称")
    private String centerProcessingName;

    @ApiModelProperty(value = "中心处理人编号")
    @Excel(name = "中心处理人编号")
    private String centralProcessingNo;

    @ApiModelProperty(value = "中心处理时间")
    @Excel(name = "中心处理时间")
    private Date centralProcessingTime;

    @ApiModelProperty(value = "附件2")
    @Excel(name = "附件2")
    private String annexTwo;

    @ApiModelProperty(value = "附件3")
    @Excel(name = "附件3")
    private String annexThree;

    @ApiModelProperty(value = "来源")
    @Excel(name = "来源")
    private String source;

    @ApiModelProperty(value = "省编码	")
    @Excel(name = "省编码	")
    private String provinceCode;

    @ApiModelProperty(value = "市编码	")
    @Excel(name = "市编码	")
    private String cityCode;

    @ApiModelProperty(value = "详细地址")
    @Excel(name = "详细地址")
    private String address;

    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "区县")
    @Excel(name = "区县")
    private String town;

    @ApiModelProperty(value = "附件1")
    @Excel(name = "附件1")
    private String annexOne;

    @ApiModelProperty(value = "责任方")
    @Excel(name = "责任方")
    private String responsibleParty;

    @ApiModelProperty(value = "创建人名称")
    @Excel(name = "创建人名称")
    private String createByName;

    @ApiModelProperty(value = "最后修改人名称")
    @Excel(name = "最后修改人名称")
    private String updateByName;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @Excel(name = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @Excel(name = "状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private BigDecimal version;

    @ApiModelProperty(value = "预留字段1")
    @Excel(name = "预留字段1")
    private String parm1;

    @ApiModelProperty(value = "预留字段2")
    @Excel(name = "预留字段2")
    private String parm2;

    @ApiModelProperty(value = "预留字段3")
    @Excel(name = "预留字段3")
    private String parm3;

    @ApiModelProperty(value = "0未上传 1已上传")
    @Excel(name = "0未上传 1已上传")
    private String uploaded;

    @ApiModelProperty(value = "登记网点编号")
    @Excel(name = "登记网点编号")
    private String registerSiteCode;

    @ApiModelProperty(value = "接收网点名称")
    @Excel(name = "接收网点名称")
    private String receiveSiteName;

    @ApiModelProperty(value = "接收网点编号")
    @Excel(name = "接收网点编号")
    private String receiveSiteCode;

    @ApiModelProperty(value = "问题件类型名称")
    @Excel(name = "问题件类型名称")
    private String problemTypeName;

    @ApiModelProperty(value = "问题件类型编码")
    @Excel(name = "问题件类型编码")
    private String problemTypeCode;

    @ApiModelProperty(value = "寄件客户编码")
    @Excel(name = "寄件客户编码")
    private String sendCusCode;

    @ApiModelProperty(value = "寄件客户名称")
    @Excel(name = "寄件客户名称")
    private String sendCusName;

    @Excel(name = "")
    private String url;

    @ApiModelProperty(value = "代收款金额")
    @Excel(name = "代收款金额")
    private BigDecimal cod;

    @ApiModelProperty(value = "目的网点名称")
    @Excel(name = "目的网点名称")
    private String destSiteName;

    @ApiModelProperty(value = "目的网点编号")
    @Excel(name = "目的网点编号")
    private String destSiteCode;

    @ApiModelProperty(value = "处理状态 1未处理，2处理中，3已完成")
    @Excel(name = "处理状态 1未处理，2处理中，3已完成")
    private Integer processingStatus;

    @ApiModelProperty(value = "处理内容")
    @Excel(name = "处理内容")
    private String processingContent;

    @Excel(name = "")
    private BigDecimal weight;

    @Excel(name = "")
    private String register;

    @Excel(name = "")
    private String registerNo;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

}
