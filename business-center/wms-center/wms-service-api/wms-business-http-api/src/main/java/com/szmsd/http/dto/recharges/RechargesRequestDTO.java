package com.szmsd.http.dto.recharges;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author liulei
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "RechargesRequestDTO")
public class RechargesRequestDTO {

    @ApiModelProperty(value = "请求参数唯一标识")
    private String serialNo;

    @ApiModelProperty(value = "充值金额")
    private RechargesRequestAmountDTO amount;

    @ApiModelProperty(value = "充值渠道")
    private String method;

    @ApiModelProperty(value = "银联银行代码")
    private String bankCode;

    @ApiModelProperty(value = "充值说明")
    private String remark;

    @ApiModelProperty(value = "回调通知地址")
    private String notifyUrl;

}
