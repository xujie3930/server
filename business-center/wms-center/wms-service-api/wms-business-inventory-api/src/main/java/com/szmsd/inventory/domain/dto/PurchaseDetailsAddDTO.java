package com.szmsd.inventory.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;


/**
 * <p>
 * 采购单
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "Purchase对象")
public class PurchaseDetailsAddDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "关联的id集合")
    @Excel(name = "关联的id集合")
    private Integer associationId;

    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品描述")
    @Excel(name = "产品描述")
    private String productDescription;

    @ApiModelProperty(value = "产品编号")
    @Excel(name = "产品编号")
    private String sku;

    @ApiModelProperty(value = "已入库数-出库单里的可用库存")
    @Excel(name = "已入库数-出库单里的可用库存")
    private Integer availableInventory;

    @ApiModelProperty(value = "采购数量")
    @Excel(name = "采购数量")
    private Integer purchaseQuantity;
    @ApiModelProperty(value = "剩余的采购数量")
    @Excel(name = "剩余的采购数量")
    private Integer remainingPurchaseQuantity;

    @ApiModelProperty(value = "已创建入库单的数量")
    @Excel(name = "已创建入库单的数量")
    private Integer quantityInStorageCreated;

    @ApiModelProperty(value = "已到仓数量")
    @Excel(name = "已到仓数量")
    private Integer arrivalQuantity;

    @ApiModelProperty(value = "附件图片地址")
    @Excel(name = "附件图片地址")
    private String attachmentUrl;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }


}
