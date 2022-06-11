package com.szmsd.returnex.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ReturnExpressAddDTO
 * @Description: 新增退货单处理
 * @Author: 11
 * @Date: 2021/3/26 16:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("服务端新增退货单DTO")
public class ReturnExpressServiceAddDTO extends ReturnExpressAddDTO {
    /**
     * ---ADD----
     */
    @NotNull(message = "过期时间不能为空")
    @ExcelProperty(value = "过期时间", index = 5)
    @ApiModelProperty(value = "过期时间")
    @Excel(name = "过期时间")
    private Date expireTime;

    @NotNull(message = "到仓时间不能为空")
    @ExcelProperty(value = "到仓时间", index = 4)
    @ApiModelProperty(value = "到仓时间")
    @Excel(name = "到仓时间")
    private Date arrivalTime;

    @ApiModelProperty(value = "处理时间")
    @ExcelIgnore
    @Excel(name = "处理时间")
    private Date processTime;
    @ExcelProperty(value = "仓库备注", index = 3)
    @ApiModelProperty(value = "仓库备注")
    private String remark;
    @ExcelIgnore
    @ApiModelProperty(value = "客户备注")
    @Excel(name = "客户备注")
    private String customerRemark;
    @ExcelIgnore
    @ApiModelProperty(value = "新出库单号-改发原处理号")
    @Excel(name = "新出库单号")
    private String fromOrderNoNew;
    @ExcelIgnore
    @ApiModelProperty(value = "新物流跟踪号-改发跟踪号")
    @Excel(name = "改发跟踪号")
    private String scanCodeNew;


    @ApiModelProperty(value = "国家代码")
    private String countryCode;


    @ApiModelProperty(value = "国家名称")
    private String country;


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
