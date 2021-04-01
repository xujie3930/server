package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import com.szmsd.returnex.config.BOConvert;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @ClassName: ReturnExpressAddDTO
 * @Description: 新增退货单处理
 * @Author: 11
 * @Date: 2021/3/26 16:45
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("新增退货单DTO")
public class ReturnExpressAddDTO implements Serializable, BOConvert {

    @Min(value = 0, message = "数据异常")
    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @NotEmpty(message = "客户代码不能为空")
    @ApiModelProperty(value = "客户代码", example = "UID123456", required = true)
    private String sellerCode;

    @ApiModelProperty(value = "退件来源[ 申请退件 VMS]", example = "RETURN_FORECAST")
    private String returnSource;

    @ApiModelProperty(value = "退件来源[ 申请退件 VMS]", example = "RETURN_FORECAST")
    private String returnSourceStr;

    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]", example = "OWN_INVENTORY_RETURN")
    private String returnType;
    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]", example = "OWN_INVENTORY_RETURN")
    private String returnTypeStr;
    @StringLength(maxLength = 50, message = "原出库单号错误")
    @ApiModelProperty(value = "退件原始单号 原出库单号", example = "SF123456")
    private String fromOrderNo;

    @NotEmpty(message = "预报单号不能为空")
    @ApiModelProperty(value = "预报单号 系统生成", required = true)
    private String expectedNo;

    @ApiModelProperty(value = "VMS处理单号", required = true)
    private String returnNo;

    @ApiModelProperty(value = "退件目标仓库编码", example = "SZ")
    private String warehouseCode;
    @ApiModelProperty(value = "退件目标仓库编码", example = "SZ")
    private String warehouseCodeStr;
    @ApiModelProperty(value = "退货渠道", example = "客户自选")
    private String returnChannel;

    @ApiModelProperty(value = "退货Tracking 号", example = "TID123456")
    private String scanCode;
    /**
     * 销毁 包裹上架 拆包检查
     */
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "Destroy", required = true)
    private String processType;
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "销毁", required = true)
    private String processTypeStr;

    @ApiModelProperty(value = "处理方式 编码", example = "Destroy",hidden = true)
    private String applyProcessMethod;
    @ApiModelProperty(value = "处理方式 编码", example = "Destroy",hidden = true)
    private String applyProcessMethodStr;

    @ApiModelProperty(value = "备注")
    private String processRemark;


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
