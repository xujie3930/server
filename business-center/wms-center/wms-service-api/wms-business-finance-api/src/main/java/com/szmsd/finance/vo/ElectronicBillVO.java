package com.szmsd.finance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(description = "电子账单")
public class ElectronicBillVO implements Serializable {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "账单开始时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date billStartTime;

    @ApiModelProperty(value = "账单结束时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date billEndTime;

    @ApiModelProperty(value = "状态 0 未处理 1 处理中 2 处理完成")
    private Integer buildStatus;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件路径")
    private String fileUrl;

    @ApiModelProperty(value = "创建人名称")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;



}
