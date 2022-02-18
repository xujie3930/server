package com.szmsd.returnex.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName: ReturnExpressAddDTO
 * @Description: 新增退货单处理
 * @Author: 11
 * @Date: 2021/3/26 16:45
 */
@Data
@EqualsAndHashCode
@ApiModel("客户端导入sku处理明细")
public class ReturnExpressClientImportSkuDTO {

    @NotBlank(message = "出库单号不能为空")
    @ExcelProperty(value = "预报单号", index = 0)
    @ApiModelProperty(value = "预报单号 系统生成", required = true)
    private String expectedNo;

    @ExcelProperty(value = "原出库单号", index = 1)
    @NotBlank(message = "原处理单号不能为空")
    @StringLength(maxLength = 50, message = "原出库单号错误")
    @ApiModelProperty(value = "退件原始单号 原出库单号 原处理号", example = "SF123456")
    private String fromOrderNo;

    @NotBlank(message = "sku不能为空")
    @ApiModelProperty(value = "SKU")
    @ExcelProperty(value = "SKU", index = 2)
    private String sku;

    @NotBlank(message = "新上架的SKU编码不能为空")
    @ApiModelProperty(value = "新上架SKU编码")
    @ExcelProperty(value = "新上架SKU编码", index = 3)
    private String putawaySku;

    @Min(value = 0, message = "上架数量最少为0")
    @NotNull(message = "上架数量不能为空")
    @ApiModelProperty(value = "上架数量")
    @ExcelProperty(value = "上架数量", index = 4)
    private Integer putawayQty;

    /**
     * SKU处理备注 0-500
     */
    @ExcelProperty(value = "客户备注",index = 5)
    @ApiModelProperty(value = "客户备注")
    private String remark;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
