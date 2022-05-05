package com.szmsd.returnex.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import com.szmsd.returnex.config.BOConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ReturnExpressAddDTO
 * @Description: 新增退货单处理
 * @Author: 11
 * @Date: 2021/3/26 16:45
 */
@Data
@EqualsAndHashCode
@ApiModel("新增退货单DTO")
public class ReturnExpressAddDTO implements Serializable, BOConvert {
    @ExcelIgnore
    @Min(value = 0, message = "数据异常")
    @ApiModelProperty(value = "主键ID")
    private Integer id;
    @ExcelIgnore
    @ApiModelProperty(value = "sku")
    private String sku;
    @ExcelIgnore
    //@NotEmpty(message = "客户代码不能为空")
    @ApiModelProperty(value = "客户代码", example = "UID123456", required = true)
    private String sellerCode;
    @ExcelIgnore
    @ApiModelProperty(value = "退件来源[ 申请退件 VMS]", example = "068001")
    private String returnSource;
    @ExcelIgnore
    @ApiModelProperty(value = "退件来源[ 申请退件 VMS]", example = "WMS退件")
    private String returnSourceStr;
    @ExcelIgnore
    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]", example = "070003")
    private String returnType;
    @ExcelIgnore
    @ApiModelProperty(value = "退件类型[ 自有库存退件 转运单退件 外部渠道退件]", example = "外部渠道退件")
    private String returnTypeStr;

    @ExcelProperty(value = "原出库单号",index = 1)
    @NotBlank(message = "原处理单号不能为空")
    @StringLength(maxLength = 50, message = "原出库单号错误")
    @ApiModelProperty(value = "退件原始单号 原出库单号 原处理号", example = "SF123456")
    private String fromOrderNo;

    @ExcelIgnore
    @ApiModelProperty(value = "预报单号 系统生成", required = true)
    private String expectedNo;

    @ExcelProperty(value = "退件单号",index = 0)
    @ApiModelProperty(value = "WMS处理单号 退件单号", required = true)
    private String returnNo;
    @ExcelIgnore
    @ApiModelProperty(value = "退件目标仓库编码", example = "SZ")
    private String warehouseCode;

    @ExcelIgnore
    @ApiModelProperty(value = "退件目标仓库", example = "SZ")
    private String warehouseCodeStr;
    @ExcelIgnore
    @ApiModelProperty(value = "退货渠道", example = "客户自选")
    private String returnChannel;
    @ExcelIgnore
//    @NotBlank(message = "退货Tracking号(原跟踪号)不能为空")
    @ApiModelProperty(value = "退货Tracking号 原跟踪号", example = "TID123456", required = true)
    private String scanCode;

    /**
     * 销毁 包裹上架 拆包检查
     */
    @ExcelIgnore
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "068002", required = true)
    private String processType;
    @ExcelIgnore
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "销毁", required = true)
    private String processTypeStr;
    @ExcelIgnore
    @ApiModelProperty(value = "处理方式 编码", example = "Destroy", hidden = true)
    private String applyProcessMethod;
    @ExcelIgnore
    @ApiModelProperty(value = "处理方式 编码", example = "Destroy", hidden = true)
    private String applyProcessMethodStr;

    @ExcelProperty(value = "退回原因",index = 2)
    @ApiModelProperty(value = "备注(退回原因)")
    private String processRemark;

    @Valid
    @ExcelIgnore
    @ApiModelProperty(value = "商品sku列表数据")
    private List<ReturnExpressGoodAddDTO> goodList;

    @ExcelIgnore
    @ApiModelProperty(value = "refNo")
    private String refNo;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
