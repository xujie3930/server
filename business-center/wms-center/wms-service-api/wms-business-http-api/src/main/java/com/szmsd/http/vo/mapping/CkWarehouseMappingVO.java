package com.szmsd.http.vo.mapping;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 仓库与仓库关联映射
 * </p>
 *
 * @author 11
 * @since 2021-12-13
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "CK1 响应对像")
public class CkWarehouseMappingVO {

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库编码")
    private String WarehouseId;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库名称")
    private String WarehouseName;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String Country;

}
