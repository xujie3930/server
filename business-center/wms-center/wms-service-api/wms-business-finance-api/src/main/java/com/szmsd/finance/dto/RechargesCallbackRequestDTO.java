package com.szmsd.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liulei
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "RechargesCallbackRequestDTO")
public class RechargesCallbackRequestDTO {

    @JsonProperty("RechargeNo")
    @ApiModelProperty(value = "充值编号")
    private String rechargeNo;

    @JsonProperty("RechargeAmount")
    @ApiModelProperty(value = "充值金额")
    private RechargesCallbackAmountDTO rechargeAmount;

    @JsonProperty("TransactionFee")
    @ApiModelProperty(value = "手续费")
    private RechargesCallbackAmountDTO transactionFee;

    @JsonProperty("ActualRechargeAmount")
    @ApiModelProperty(value = "实际到账金额")
    private RechargesCallbackAmountDTO actualRechargeAmount;

    @JsonProperty("Status")
    @ApiModelProperty(value = "充值状态")
    private String status;

    @JsonProperty("RechargeUrl")
    @ApiModelProperty(value = "第三方充值地址")
    private String rechargeUrl;

    @JsonProperty("SerialNo")
    @ApiModelProperty(value = "请求参数唯一标识")
    private String serialNo;

    @JsonProperty("Remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @JsonProperty("Message")
    @ApiModelProperty(value = "返回消息")
    private String message;

    @JsonProperty("Code")
    @ApiModelProperty(value = "错误编码")
    private String code;
}
