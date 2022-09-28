package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccountBillRecordTaskResultVO implements Serializable {

    @ApiModelProperty(value = "业务ID")
    private String recordId;

    @ApiModelProperty(value = "文件URL")
    private String fileUrl;

    @ApiModelProperty(value = "")
    private String fileName;

}
