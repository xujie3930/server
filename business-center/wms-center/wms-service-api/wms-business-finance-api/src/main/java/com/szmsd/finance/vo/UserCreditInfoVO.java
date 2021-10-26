package com.szmsd.finance.vo;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.finance.enums.CreditConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @ClassName: CreditInfoBO
 * @Description: 授信额度信息
 * @Author: 11
 * @Date: 2021-09-07 14:56
 */
@Data
@ApiModel(description = "用户授信额度信息")
public class UserCreditInfoVO {
    /**
     * 09-07 授信额度新增
     */
    @ApiModelProperty(value = "授信类型(QUOTA：额度，TIME_LIMIT：期限)")
    @Excel(name = "授信类型(QUOTA：额度，TIME_LIMIT：期限)")
    private String creditType;

    @ApiModelProperty(value = "授信额度")
    @Excel(name = "授信额度")
    private BigDecimal creditLine;

    @ApiModelProperty(value = "授信时间间隔")
    @Excel(name = "授信时间间隔")
    private Integer creditTimeInterval;
    @ApiModelProperty(value = "授信缓冲时间间隔")
    @Excel(name = "授信缓冲时间间隔")
    private Integer creditBufferTimeInterval;
    @ApiModelProperty(value = "币种编码", required = true)
    private String currencyCode;
    @ApiModelProperty(value = "币种名")
    private String currencyName;
    @ApiModelProperty(value = "客户编码", required = true)
    private String cusCode;
    @ApiModelProperty(value = "授信状态", required = true)
    private Integer creditStatus;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
