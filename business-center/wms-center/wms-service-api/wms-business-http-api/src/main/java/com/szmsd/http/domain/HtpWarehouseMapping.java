package com.szmsd.http.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
 * <p>
 * 仓库与仓库关联映射
 * </p>
 *
 * @author 11
 * @since 2021-12-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "仓库与仓库关联映射", description = "HtpWarehouseMapping对象")
public class HtpWarehouseMapping extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    private String delFlag;

    @ApiModelProperty(value = "服务组")
    @Excel(name = "服务组")
    private String groupId;

    @ApiModelProperty(value = "源系统")
    @Excel(name = "源系统")
    private String originSystem;

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "仓库国家")
    @Excel(name = "仓库国家")
    private String warehouseCountry;

    @ApiModelProperty(value = "目标系统")
    @Excel(name = "目标系统")
    private String mappingSystem;

    @ApiModelProperty(value = "目标仓库名称")
    @Excel(name = "目标仓库名称")
    private String mappingWarehouseName;

    @ApiModelProperty(value = "目标仓库编码")
    @Excel(name = "目标仓库编码")
    private String mappingWarehouseCode;

    @ApiModelProperty(value = "目标仓库国家")
    @Excel(name = "目标仓库国家")
    private String mappingWarehouseCountry;

    @ApiModelProperty(value = "启用状态(0:禁用,1:启用)")
    @Excel(name = "启用状态(0:禁用,1:启用)")
    private Integer status;


}
