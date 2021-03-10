package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 9:50
 */
@Data
@ApiModel(value = "HtpTransactionDto", description = "HtpTransactionDto对象")
public class HtpTransactionDto implements Serializable {

    @ApiModelProperty(value = "单据号")
    private String invoiceNo;

    @ApiModelProperty(value = "单据类型")
    private String invoiceType;
}
