package com.szmsd.returnex.dto.wms;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: ReturnProcessingFinishReqDTO
 * @Description: 接收WMS完成的处理对象
 * @Author: 11
 * @Date: 2021-04-21 17:02
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("接收WMS完成的处理对象")
public class ReturnProcessingFinishReqDTO {

    /**
     * 仓库退件单号，唯一，WMS生成 1-50
     */
    @StringLength(minLength = 1, maxLength = 50, message = "仓库退件单号超过约定长度[1-50]")
    @NotBlank(message = "仓库退件单号不能为空")
    @ApiModelProperty(value = "仓库退件单号，唯一，WMS生成", example = "VMS12345")
    private String returnNo;


    /**
     * string
     * maxLength: 500
     * minLength: 0
     * nullable: true
     * 备注
     */
    @StringLength(maxLength = 500, message = "备注超过约定长度[1-500]")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "仓库", example = "SZ")
    private String warehouseCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
