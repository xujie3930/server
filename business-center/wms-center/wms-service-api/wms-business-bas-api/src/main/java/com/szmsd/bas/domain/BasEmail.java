package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "", description = "BasEmail表功能")
public class BasEmail {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "CBD")
    private String serviceManagerName;

    @ApiModelProperty(value = "CS")
    private String serviceStaffName;

    @ApiModelProperty(value = "order_No")
    private String orderNo;

    @ApiModelProperty(value = "原始单号")
    private String noTrackingNo;

    @ApiModelProperty(value = "新单号")
    private String trackingNo;

    @ApiModelProperty(value = "邮箱接收人")
    private String empTo;

    @ApiModelProperty(value = "员工编号")
    private String empCode;

    @ApiModelProperty(value = "0表示包裹查询导入更新挂号，1（其他功能）")
    private Integer modularType;

    @ApiModelProperty(value = "发送状态（0表示未发送，1已发送）")
    private Integer sendType;



}