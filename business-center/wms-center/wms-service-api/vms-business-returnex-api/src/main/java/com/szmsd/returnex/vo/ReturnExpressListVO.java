package com.szmsd.returnex.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    @TableId(value = "id")
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "退件原始单号 原出库单号")
    @Excel(name = "退件原始单号 原出库单号")
    private String fromOrderNo;

    @ApiModelProperty(value = "退件可扫描编码")
    @Excel(name = "退件可扫描编码")
    private String scanCode;

    @ApiModelProperty(value = "入库方式编码")
    @Excel(name = "入库方式编码")
    private String warehouseMethodCode;

    @ApiModelProperty(value = "预报单号")
    @Excel(name = "预报单号")
    private String expectedNo;

    @ApiModelProperty(value = "VMS处理单号")
    @Excel(name = "VMS处理单号")
    private String vmsProcessNumber;

    @ApiModelProperty(value = "申请处理方式")
    @Excel(name = "申请处理方式")
    private String processType;

    @ApiModelProperty(value = "类型[ 退件预报，VMS通知退件]")
    @Excel(name = "类型[ 退件预报，VMS通知退件]")
    private String returnType;

    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]")
    @Excel(name = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]")
    private String returnSubType;

    @ApiModelProperty(value = "退件目标仓库编码")
    @Excel(name = "退件目标仓库编码")
    private String returnDestinationWarehouse;

    @ApiModelProperty(value = "申请处理方式编码")
    @Excel(name = "申请处理方式编码")
    private String applyProcessMethod;

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
    private Integer returnSource;

    @ApiModelProperty(value = "处理状态编码")
    @Excel(name = "处理状态编码")
    private String dealStatus;

    @ApiModelProperty(value = "退货Tracking 号")
    @Excel(name = "退货Tracking 号")
    private String returnTracking;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
