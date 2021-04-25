package com.szmsd.inventory.domain;

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
 * 采购单
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "采购单", description = "Purchase对象")
public class Purchase extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "关联的id集合")
    @Excel(name = "关联的id集合")
    private String assertId;

    @ApiModelProperty(value = "创建人编号")
    @Excel(name = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    @Excel(name = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Integer version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "卖家代码")
    @Excel(name = "卖家代码")
    private String sellerCode;

    @ApiModelProperty(value = "出库后重新上架的新SKU编码")
    @Excel(name = "出库后重新上架的新SKU编码")
    private String newSku;
    @ApiModelProperty(value = "申报品名-产品描述")
    @Excel(name = "申报品名")
    private String productName;
    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "采购单号")
    @Excel(name = "采购单号")
    private String purchaseNo;

    @ApiModelProperty(value = "已入库数-出库单里的可用库存")
    @Excel(name = "已入库数-出库单里的可用库存")
    private String availableInventory;

    @ApiModelProperty(value = "采购数量")
    @Excel(name = "采购数量")
    private String purchaseQuantity;

    @ApiModelProperty(value = "已创建入库单的数量")
    @Excel(name = "已创建入库单的数量")
    private String quantityInStorageCreated;

    @ApiModelProperty(value = "已到仓数量")
    @Excel(name = "已到仓数量")
    private String arrivalQuantity;


}
