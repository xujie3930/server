package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.finance.enums.RefundStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName: ConfirmOperationDTO
 * @Description: 系统确认操作
 * @Author: 11
 * @Date: 2021-08-13 16:44
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "退费审核")
public class RefundReviewDTO {

    @ApiModelProperty(value = "审核的数据")
    @NotEmpty(message = "请勾选要审核的数据")
    private List<String> idList;

    @ApiModelProperty(value = "审核驳回原因")
    private String reviewRemark;

    @NotNull(message = "审核结果不能空")
    @ApiModelProperty(value = "审核的结果")
    private RefundStatusEnum status;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
