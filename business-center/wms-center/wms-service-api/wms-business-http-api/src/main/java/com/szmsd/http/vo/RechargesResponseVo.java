package com.szmsd.http.vo;

import com.szmsd.http.dto.recharges.RechargesRequestAmountDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author liulei
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "RechargesResponseVo")
public class RechargesResponseVo{

    @ApiModelProperty(value = "充值编号")
    private String rechargeNo;

    @ApiModelProperty(value = "充值金额")
    private RechargesRequestAmountDTO rechargeAmount;

    @ApiModelProperty(value = "手续费")
    private RechargesRequestAmountDTO transactionFee;

    @ApiModelProperty(value = "实际到账金额")
    private RechargesRequestAmountDTO actualRechargeAmount;

    @ApiModelProperty(value = "充值状态")
    private String status;

    @ApiModelProperty(value = "第三方充值地址")
    private String rechargeUrl;

    @ApiModelProperty(value = "请求参数唯一标识")
    private String serialNo;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "返回消息")
    private String Message;

    @ApiModelProperty(value = "错误编码")
    private String Code;
}
