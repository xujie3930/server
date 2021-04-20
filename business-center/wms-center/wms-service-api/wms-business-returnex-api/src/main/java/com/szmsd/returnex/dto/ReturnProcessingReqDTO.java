package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.validator.annotation.StringLength;
import com.szmsd.http.dto.returnex.ReturnDetail;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName: ReturnArrivalReqDTO
 * @Description: 接受WMS库存信息
 * @Author: 11
 * @Date: 2021/3/27 10:48
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("接受WMS仓库退件处理结果")
public class ReturnProcessingReqDTO {
    @ApiModelProperty(value = "账号(OMS提供)")
    private String appId;
    @ApiModelProperty(value = "密码(OMS提供)")
    private String sign;
    @ApiModelProperty(value = "操作人")
    private String operator;
    @ApiModelProperty(value = "操作时间")
    private String operateOn;
    @ApiModelProperty(value = "业务主键,用来做幂等校验")
    private String transactionId;
    /**
     * string
     * nullable: true
     * 仓库
     */
    //@StringLength(minLength = 1, maxLength = 50, message = "仓库退件单号超过约定长度[1-50]")
    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    /**
     * string
     * maxLength: 50
     * minLength: 1
     * 仓库退件流水号
     */
    @StringLength(minLength = 1, maxLength = 50, message = "仓库退件流水号超过约定长度[1-50]")
    @NotBlank(message = "仓库退件流水号不能为空")
    @ApiModelProperty(value = "仓库退件流水号", required = true)
    private String returnNo;

    @ApiModelProperty(value = "退件明细")
    private List<ReturnDetail> details;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
