package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasTransactionDTO", description = "接口业务主键")
public class BasTransactionDTO {

    @ApiModelProperty(value = "接口")
    private String apiCode;

    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    private String transactionId;

}
