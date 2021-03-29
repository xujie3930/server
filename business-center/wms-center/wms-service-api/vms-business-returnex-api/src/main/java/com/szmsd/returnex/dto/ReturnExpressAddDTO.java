package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.validator.annotation.StringLength;
import com.szmsd.returnex.domain.BOConvert;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码", example = "UID123456")
    private String sellerCode;

    @ApiModelProperty(value = "退件来源[ 申请退件 VMS]", example = "RETURN_FORECAST")
    private ReturnExpressEnums.ReturnSourceEnum returnSource;

    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]", example = "OWN_INVENTORY_RETURN")
    private ReturnExpressEnums.ReturnTypeEnum returnType;

    @StringLength(maxLength = 50, message = "原出库单号错误")
    @ApiModelProperty(value = "退件原始单号 原出库单号", example = "SF123456")
    private String fromOrderNo;

    //TODO 生成规则
    @ApiModelProperty(value = "预报单号 系统生成", hidden = true)
    private String expectedNo = "xxxxx";

    @ApiModelProperty(value = "VMS处理单号")
    private String returnNo;

    @ApiModelProperty(value = "退件目标仓库编码", example = "SZ")
    private String warehouseCode;

    @ApiModelProperty(value = "退货渠道", example = "客户自选")
    private String returnChannel;

    @ApiModelProperty(value = "退货Tracking 号", example = "TID123456")
    private String returnTracking;
    /**
     * 销毁 包裹上架 拆包检查
     */
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "Destroy")
    private ReturnExpressEnums.ProcessTypeEnum processType;

    @ApiModelProperty(value = "处理方式 编码", example = "Destroy")
    private ReturnExpressEnums.ApplyProcessMethodEnum applyProcessMethod;

    @ApiModelProperty(value = "备注")
    private String processRemark;


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
