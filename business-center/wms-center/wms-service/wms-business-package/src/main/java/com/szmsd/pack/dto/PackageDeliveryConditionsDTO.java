package com.szmsd.pack.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * <p>
 * 发货条件表
 * </p>
 *
 * @author admpon
 * @since 2022-03-23
 */

@Data
@ApiModel(value = "WarehouseOperationDTO", description = "WarehouseOperationDTO对象")
public class PackageDeliveryConditionsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "产品代码")
    @ExcelProperty(value = "产品代码", index = 0)
    private String productCode;

    @ApiModelProperty(value = "仓库代码")
    @ExcelProperty(value = "仓库代码", index = 1)
    private String warehouseCode;

    @ApiModelProperty(value = "仓库中文名(冗余字段)")
    @ExcelProperty(value = "仓库中文名(冗余字段)", index = 2)
    private String warehouseNameCn;

    @ApiModelProperty(value = "仓库英文名(冗余字段)")
    @ExcelProperty(value = "仓库英文名(冗余字段)", index = 3)
    private String warehouseNameEn;

    @ApiModelProperty(value = "指令下发节点代码")
    @ExcelProperty(value = "指令下发节点代码", index = 4)
    private String commandNodeCode;

    @ApiModelProperty(value = "指令下发节点名称")
    @ExcelProperty(value = "指令下发节点名称", index = 5)
    private String commandNodeName;

    @ApiModelProperty(value = "包材是否回传（0：否，1：是）")
    @ExcelProperty(value = "包材是否回传（0：否，1：是）", index = 6)
    private String packageReturned;

    @ApiModelProperty(value = "重量是否回传（0：否，1：是）")
    @ExcelProperty(value = "重量是否回传（0：否，1：是）", index = 7)
    private String weightReturned;

    @ApiModelProperty(value = "是否仓库贴标（0：否，1：是）")
    @ExcelProperty(value = "是否仓库贴标（0：否，1：是）", index = 8)
    private String warehouseLabeling;

    @ApiModelProperty(value = "状态")
    @ExcelProperty(value = "状态", index = 9)
    private String status;


}
