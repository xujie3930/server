package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.finance.enums.CreditConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @ClassName: CreditInfoBO
 * @Description: 授信额度信息
 * @Author: 11
 * @Date: 2021-09-07 14:56
 */
@Data
@ApiModel(description = "用户授信额度信息")
public class UserCreditDTO {
    /**
     * 09-07 授信额度新增
     */
    @ApiModelProperty(value = "授信类型(0：额度，1：期限)")
    @Excel(name = "授信类型(0：额度，1：期限)")
    private Integer creditType;

    @ApiModelProperty(value = "授信额度")
    @Excel(name = "授信额度")
    private BigDecimal creditLine;

    @ApiModelProperty(value = "授信时间间隔")
    @Excel(name = "授信时间间隔")
    private Integer creditTimeInterval;
    @NotBlank
    @ApiModelProperty(value = "币种编码",required = true)
    private String currencyCode;
    @ApiModelProperty(value = "币种名")
    private String currencyName;
    @NotBlank
    @ApiModelProperty(value = "客户编码",required = true)
    private String cusCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
