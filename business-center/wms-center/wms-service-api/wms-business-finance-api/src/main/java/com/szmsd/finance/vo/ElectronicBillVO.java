package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "电子账单")
public class ElectronicBillVO implements Serializable {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "账单开始时间")
    private String startTime;

    @ApiModelProperty(value = "账单结束时间")
    private String entTime;

    @ApiModelProperty(value = "状态 0 未处理 1 处理中 2 处理完成")
    private Integer buildStatus;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    @ApiModelProperty(value = "创建人名称")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private String createTime;



}
