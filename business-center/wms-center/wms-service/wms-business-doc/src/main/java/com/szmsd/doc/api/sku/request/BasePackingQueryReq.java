package com.szmsd.doc.api.sku.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: BasePackingQueryReq
 * @Description:
 * @Author: 11
 * @Date: 2021-09-18 15:02
 */
@Data
@ApiModel(description = "包材查询条件")
public class BasePackingQueryReq {
    @ApiModelProperty(value = "仓库信息", example = "NJ")
    private String warehouseCode;

}
