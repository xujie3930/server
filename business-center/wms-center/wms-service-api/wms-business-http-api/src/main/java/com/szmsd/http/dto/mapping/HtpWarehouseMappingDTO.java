package com.szmsd.http.dto.mapping;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


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
@ApiModel(description = "HtpWarehouseMappingVO新增/修改对象")
public class HtpWarehouseMappingDTO {

    @ApiModelProperty(value = "id")
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "源系统", hidden = true)
    @Excel(name = "源系统")
    private String originSystem;

    @NotBlank(message = "仓库编码不能为空")
    @ApiModelProperty(value = "仓库编码", required = true)
    @Excel(name = "仓库编码")
    private String warehouseCode;

    @NotBlank(message = "仓库名称不能为空")
    @ApiModelProperty(value = "仓库名称", required = true)
    @Excel(name = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "目标系统", hidden = true)
    @Excel(name = "目标系统")
    private String mappingSystem;

    @NotBlank(message = "目标仓库名称不能为空")
    @ApiModelProperty(value = "目标仓库名称", required = true)
    @Excel(name = "目标仓库名称")
    private String mappingWarehouseName;

    @NotBlank(message = "目标仓库编码不能为空")
    @ApiModelProperty(value = "目标仓库编码", required = true)
    @Excel(name = "目标仓库编码")
    private String mappingWarehouseCode;

    @NotNull(message = "启用状态不能为空")
    @ApiModelProperty(value = "启用状态(0:禁用,1:启用)", required = true)
    @Excel(name = "启用状态(0:禁用,1:启用)")
    private Integer status;


}
