package com.szmsd.inventory.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 采购单日志
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "采购单日志", description = "PurchaseLog对象")
public class PurchaseLogVO {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "关联的id集合")
    @Excel(name = "关联的id集合")
    private Integer associationId;

    @ApiModelProperty(value = "类型（0:创建采购单，1：创建入库单）")
    @Excel(name = "类型（0:创建采购单，1：创建入库单）")
    private Integer type;

    @ApiModelProperty(value = "入库单号")
    @Excel(name = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "入库单号")
    @Excel(name = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "日志内容")
    @Excel(name = "日志内容")
    private String logDetails;

    @ApiModelProperty(value = "采购单号")
    @Excel(name = "采购单号")
    private String purchaseNo;

}
