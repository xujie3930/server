package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: ReturnArrivalReqDTO
 * @Description: 接受VMS库存信息
 * @Author: 11
 * @Date: 2021/3/27 10:48
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("接受VMS仓库退件处理结果")
public class ReturnProcessingReqDTO {

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
    @ApiModelProperty(value = "仓库退件流水号")
    private String returnNo;

    /**
     * 处理方式
     * 销毁：Destroy
     * 整包上架：PutawayByPackage
     * 拆包检查：OpenAndCheck
     * 按明细上架：PutawayBySku
     */
    @ApiModelProperty(value = "处理方式")
    private ReturnExpressEnums.ProcessTypeEnum processType;

    @ApiModelProperty(value = "处理方式")
    private String remark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
