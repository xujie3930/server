package com.szmsd.http.dto.mapping;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;


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
@ApiModel(description = "HtpWarehouseMappingQueryDTO对象")
public class HtpWarehouseMappingQueryDTO implements Serializable {

    @ApiModelProperty("当前页，从1开始，默认为1")
    private int pageNum = 1;

    @ApiModelProperty("每页的数量，默认为10")
    private int pageSize = 10;

    @ApiModelProperty(value = "源系统")
    @Excel(name = "源系统")
    private String originSystem;

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "目标系统")
    @Excel(name = "目标系统")
    private String mappingSystem;

    @ApiModelProperty(value = "目标仓库名称")
    @Excel(name = "目标仓库名称")
    private String mappingWarehouseName;

    @ApiModelProperty(value = "目标仓库编码")
    @Excel(name = "目标仓库编码")
    private String mappingWarehouseCode;

    @TableField(value = "`status`")
    @ApiModelProperty(value = "启用状态(0:禁用,1:启用)")
    @Excel(name = "启用状态(0:禁用,1:启用)")
    private Integer status;


}
