package com.szmsd.doc.api.warehouse.req;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasWarehouseQueryReq", description = "仓库列表查询条件")
public class BasWarehouseQueryReq extends QueryDto {

    @Size(max = 30, message = "仓库代码 仅支持 0-30 字符")
    @ApiModelProperty(value = "仓库代码 [30]", example = "GZ",hidden = true)
    private String warehouseCode;

    @Size(max = 200, message = "仓库中文名 仅支持 0-200 字符")
    @ApiModelProperty(value = "仓库中文名 [200]", example = "广州",hidden = true)
    private String warehouseNameCn;

    //    @Size(max = 30, message = "国家 仅支持 0-30 字符")
//    @ApiModelProperty(value = "国家 - 代码 [30]")
//    private String countryCode;
//    @Size(max = 200, message = "省 仅支持 0-200 字符")
//    @ApiModelProperty(value = "省 [200]")
//    private String province;
//    @Size(max = 200, message = "城市 仅支持 0-200 字符")
//    @ApiModelProperty(value = "城市 [200]")
//    private String city;

    @Size(max = 10, message = "状态 仅支持 0-10 字符")
    @ApiModelProperty(value = "状态：0无效，1有效 [10]", allowableValues = "0,1", example = "0",hidden = true)
    private String status = "1";

}