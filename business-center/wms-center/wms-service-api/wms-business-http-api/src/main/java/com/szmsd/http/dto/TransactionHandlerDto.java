package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 9:57
 */
@Data
@ApiModel(value = "TransactionHandlerDto", description = "TransactionHandlerDto对象")
public class TransactionHandlerDto {

    @ApiModelProperty(value = "单据号")
    private String invoiceNo;

    @ApiModelProperty(value = "单据类型")
    private String invoiceType;
}
