package com.szmsd.pack.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
 * <p>
 * package - 交货管理 - 揽收货物
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "package - 交货管理 - 揽收货物", description = "PackageCollectionDetail对象")
public class PackageCollectionDetail extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Long id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Long version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private Integer delFlag;

    @ApiModelProperty(value = "揽收ID")
    @Excel(name = "揽收ID")
    private Long collectionId;

    @ApiModelProperty(value = "顺序")
    @Excel(name = "顺序")
    private Integer sort;

    @ApiModelProperty(value = "SKU")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "SKU名称")
    @Excel(name = "SKU名称")
    private String skuName;

    @ApiModelProperty(value = "数量")
    @Excel(name = "数量")
    private Integer qty;

    @ApiModelProperty(value = "重量 g")
    @Excel(name = "重量 g")
    private BigDecimal weight;

    @ApiModelProperty(value = "长 cm")
    @Excel(name = "长 cm")
    private BigDecimal length;

    @ApiModelProperty(value = "宽 cm")
    @Excel(name = "宽 cm")
    private BigDecimal width;

    @ApiModelProperty(value = "高 cm")
    @Excel(name = "高 cm")
    private BigDecimal height;

}
