package com.szmsd.delivery.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value="查件服务导出对象", description="DelQueryService对象")
public class DelQueryServiceExc {


    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    @Excel(name="订单号",width = 30,needMerge = true)
    private String orderNo;

    @ApiModelProperty(value = "跟踪ID")
    @Excel(name="跟踪号",width = 30,needMerge = true)
    private String traceId;

    @ApiModelProperty(value = "refno")
    @Excel(name="refNo",width = 30,needMerge = true)
    private String refNo;

    @ApiModelProperty(value = "物流服务编号")
    private String shipmentRule;

    @ApiModelProperty(value = "物流服务名称")
    @Excel(name="物流服务名称",width = 30,needMerge = true)
    private String shipmentService;

    @ApiModelProperty(value = "国家名称")
    @Excel(name="国家名称",width = 30,needMerge = true)
    private String country;

    @ApiModelProperty(value = "客户代码")
    @Excel(name="客户代码",width = 30,needMerge = true)
    private String sellerCode;


    @ApiModelProperty(value = "业务经理-id")
    private String serviceManager;

    @ApiModelProperty(value = "业务经理-编码")
    private String serviceManagerName;

    @ApiModelProperty(value = "业务经理-名称")
    @Excel(name="业务经理",width = 30,needMerge = true)
    private String serviceManagerNickName;


    @ApiModelProperty(value = "客服-id")
    private String serviceStaff;

    @ApiModelProperty(value = "客服姓名-编码")
    private String serviceStaffName;

    @ApiModelProperty(value = "客服姓名")
    @Excel(name="客服姓名",width = 30,needMerge = true)
    private String serviceStaffNickName;

    @ApiModelProperty(value = "查件原因")
    @Excel(name="查件原因",width = 30,needMerge = true)
    private String reason;

    @ApiModelProperty(value = "备注")
    @Excel(name="备注",width = 30,needMerge = true)
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name="创建时间",width = 30,needMerge = true)
    private String createTime;

    @ApiModelProperty(value = "最新轨迹")
    @Excel(name="最新轨迹",width = 40,needMerge = true)
    private String trackingDescription;

    @ApiModelProperty(value = "轨迹时间")
    @Excel(name="轨迹时间",width = 30,needMerge = true)
    private String trackingTime;

    /**
     集合
     **/
    @ExcelCollection(name = "反馈明细")
    private List<DelQueryServiceFeedbackExc> delQueryServiceFeedbackExcs;


}
