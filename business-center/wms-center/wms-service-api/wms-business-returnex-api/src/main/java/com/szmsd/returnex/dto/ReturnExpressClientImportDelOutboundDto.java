package com.szmsd.returnex.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.validator.annotation.StringLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * {@link com.szmsd.delivery.dto.DelOutboundDto}
 * 重派导入对象
 *
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(description = "重派对象")
public class ReturnExpressClientImportDelOutboundDto implements Serializable {

    @NotBlank(message = "出库单号不能为空")
    @ExcelProperty(value = "预报单号", index = 0)
    @ApiModelProperty(value = "预报单号 系统生成", required = true)
    private String expectedNo;
    @ExcelIgnore
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "068002", required = true)
    private String processType;
    @ExcelProperty(value = "申请处理方式", index = 1)
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "销毁", required = true)
    private String processTypeStr;

    @ExcelProperty(index = 2)
    @ApiModelProperty(value = "库存优先满足该订单")
    private String isFirstStr;
    @ExcelProperty(value = "收件人", index = 3)
    @NotBlank(message = "收件人不能为空")
    @ApiModelProperty(value = "收件人")
    private String consignee;
    @ExcelProperty(index = 4)
    @ApiModelProperty(value = "电话号码")
    private String phoneNo;
    @ExcelProperty(index = 5)
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ExcelProperty(index = 6)
    @ApiModelProperty(value = "增值税号")
    private String ioss;
    @ExcelProperty(index = 7)
    @ApiModelProperty(value = "COD")
    private BigDecimal codAmount;
    @ExcelProperty(index = 8)
    @ApiModelProperty(value = "街道1")
    private String street1;
    @ExcelProperty(index = 9)
    @ApiModelProperty(value = "街道2")
    private String street2;
    @ExcelProperty(index = 10)
    @ApiModelProperty(value = "城市")
    private String city;
    @ExcelProperty(index = 11)
    @ApiModelProperty(value = "省份/州")
    private String stateOrProvince;
    @ExcelProperty(index = 12)
    @ApiModelProperty(value = "国家名称")
    private String country;
    @ExcelProperty(index = 13)
    @ApiModelProperty(value = "邮编")
    private String postCode;
    @ExcelProperty(index = 14)
    @ApiModelProperty(value = "物流服务")
    private String shipmentRule;
//    @ExcelProperty(index = 14)
//    @ApiModelProperty(value = "refno")
//    private String refNo;
    @ExcelProperty(index = 15)
    @ApiModelProperty(value = "备注")
    private String remark;


}
