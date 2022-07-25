package com.szmsd.chargerules.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.chargerules.domain.WarehouseOperationDetails;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "WarehouseOperationDTO", description = "WarehouseOperationDTO对象")
public class WarehouseOperationDTO implements Serializable {

    @ExcelIgnore
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "ID", hidden = true)
    @ExcelProperty(value = "业务费用顺序", index = 0)
    private Integer rowId;

    @ExcelProperty(index = 3, value = "仓库")
    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ExcelProperty(index = 4, value = "币别")
    @ApiModelProperty(value = "币种编码")
    private String currencyCode;
    @ExcelIgnore
    @ApiModelProperty(value = "币种名称")
    private String currencyName;

    @ExcelProperty(index = 7, value = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;

    @ExcelIgnore
    @ApiModelProperty(value = "客户类型编码")
    @Excel(name = "客户类型编码")
    private String cusTypeCode;

    @ExcelProperty(value = "客户类型", index = 1)
    @ApiModelProperty(value = "客户类型编码")
    private String cusTypeName;

    @ExcelProperty(value = "客户名称", index = 2)
    @ApiModelProperty(value = "客户名称 A,B")
    @Excel(name = "客户名称 A,B")
    private String cusNameList;

    @ExcelIgnore
    @ApiModelProperty(value = "客户编码 CNI1,CNI2")
    @Excel(name = "客户编码 CNI1,CNI2")
    private String cusCodeList;

    @ExcelProperty(value = "生效时间", index = 5)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生效时间")
    @Excel(name = "生效时间")
    private Date effectiveTime;

    @ExcelProperty(value = "失效时间", index = 6)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "失效时间")
    @Excel(name = "失效时间")
    private Date expirationTime;

    @ExcelIgnore
    @ApiModelProperty(value = "库存折扣详情")
    private List<WarehouseOperationDetails> details;


    @ExcelIgnore
    @ApiModelProperty(value = "库位折扣详情")
    private List<WarehouseOperationDetails> locationDetails;


}
