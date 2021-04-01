package com.szmsd.returnex.vo;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @ClassName: ReturnExpressVO
 * @Description: 退款单列表对象
 * @Author: 11
 * @Date: 2021/3/26 13:41
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ReturnExpressVO", description = "退货单列表详情VO")
public class ReturnExpressListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "退件原始单号 原出库单号")
    private String fromOrderNo;

    @ApiModelProperty(value = "退件可扫描编码")
    private String scanCode;

    @ApiModelProperty(value = "预报单号")
    private String expectedNo;

    @ApiModelProperty(value = "VMS处理单号")
    private String returnNo;

    @ApiModelProperty(value = "申请处理方式")
    private String processType;
    @ApiModelProperty(value = "申请处理方式", hidden = true)
    private String processTypeStr;

    public void setProcessType(String processType) {
        this.processType = processType;
        Optional.ofNullable(processType)
                .filter(StringUtils::isNotEmpty)
                .ifPresent(x -> processTypeStr = ReturnExpressEnums.ProcessTypeEnum.valueOf(x).getDesc());
    }

    @ApiModelProperty(value = "退件单类型[ 自有库存退件 转运单退件 外部渠道退件]")
    private String returnType;
    @ApiModelProperty(value = "退件单类型", hidden = true)
    private String returnTypeStr;

    public void setReturnType(String returnType) {
        this.returnType = returnType;
        Optional.ofNullable(returnType)
                .filter(StringUtils::isNotEmpty)
                .ifPresent(x -> returnTypeStr = ReturnExpressEnums.ReturnTypeEnum.valueOf(x).getDesc());
    }

    @ApiModelProperty(value = "实际处理方式编码")
    private String applyProcessMethod;

    @ApiModelProperty(value = "到仓时间")
    private LocalDateTime arrivalTime;

    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "是否逾期")
    private String overdue;

    public void setOverdue(String overdue) {
        this.overdue = overdue;
        Optional.ofNullable(overdue)
                .filter(StringUtils::isNotEmpty)
                .ifPresent(x -> this.overdueStr = ReturnExpressEnums.OverdueEnum.getDesc(x));
    }

    @ApiModelProperty(value = "是否逾期", hidden = true)
    private String overdueStr;
    @ApiModelProperty(value = "处理备注")
    private String processRemark;

    @ApiModelProperty(value = "类型[默认：1：退件预报，2：VMS通知退件]")
    private String returnSource;

    @ApiModelProperty(value = "退件单来源[默认：1：退件预报2：VMS通知退件]", hidden = true)
    private String returnSourceStr;

    public void setReturnSource(String returnSource) {
        this.returnSource = returnSource;
        Optional.ofNullable(returnSource)
                .filter(StringUtils::isNotEmpty)
                .ifPresent(x -> returnSourceStr = ReturnExpressEnums.ReturnSourceEnum.valueOf(x).getDesc());

    }

    @ApiModelProperty(value = "处理状态编码")
    private String dealStatus;

    @ApiModelProperty(value = "处理状态编码", hidden = true)
    private String dealStatusStr;

    public void setDealStatus(String dealStatus) {
        this.dealStatus = dealStatus;
        Optional.ofNullable(dealStatus)
                .filter(StringUtils::isNotEmpty)
                .ifPresent(x -> dealStatusStr = ReturnExpressEnums.DealStatusEnum.valueOf(x).getDesc());
    }

    @ApiModelProperty(value = "目的仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "目的仓库名称", hidden = true)
    private String warehouseNameStr;


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
