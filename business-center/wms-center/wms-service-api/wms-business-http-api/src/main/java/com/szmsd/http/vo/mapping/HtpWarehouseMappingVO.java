package com.szmsd.http.vo.mapping;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "HtpWarehouseMappingVO对象")
public class HtpWarehouseMappingVO implements Serializable {

    @ApiModelProperty(value = "id")
    @Excel(name = "id")
    private Integer id;

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

    public HtpWarehouseMappingVO(CkWarehouseMappingVO ckWarehouseMappingVO) {
        this.mappingWarehouseName = ckWarehouseMappingVO.getWarehouseName();
        this.mappingWarehouseCode = ckWarehouseMappingVO.getWarehouseId();
        this.mappingWarehouseCountry = ckWarehouseMappingVO.getCountry();
    }
}
