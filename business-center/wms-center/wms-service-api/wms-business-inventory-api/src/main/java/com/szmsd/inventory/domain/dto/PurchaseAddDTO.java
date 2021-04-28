package com.szmsd.inventory.domain.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.inventory.config.IBOConvert;
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
public class PurchaseAddDTO implements IBOConvert {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "采购单号")
    @Excel(name = "采购单号")
    private String purchaseNo;

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

    @ApiModelProperty(value = "目标仓库code", required = true)
    @Excel(name = "目标仓库code")
    private String warehouseCode;

    @ApiModelProperty(value = "目标仓库", required = true)
    @Excel(name = "目标仓库")
    private String warehouseName;

    @ApiModelProperty(value = "出库方式编码", required = true)
    @Excel(name = "出库方式编码")
    private String orderType;

    @ApiModelProperty(value = "出库方式名", required = true)
    @Excel(name = "出库方式名")
    private String orderTypeName;

    @ApiModelProperty(value = "送货方式", required = true)
    @Excel(name = "送货方式")
    private String deliveryWayName;

    @ApiModelProperty(value = "送货方式编码", required = true)
    @Excel(name = "送货方式编码")
    private String deliveryWay;

    @ApiModelProperty(value = "出库单号 - 前端缓存携带 , 拼接", required = true)
    @Excel(name = "出库单号")
    private List<String> orderNo;

    @ApiModelProperty(value = "VAT")
    @Excel(name = "VAT")
    private String vat;

    @ApiModelProperty(value = "类别")
    @Excel(name = "类别")
    private String warehouseCategoryName;

    @ApiModelProperty(value = "类别编码")
    @Excel(name = "类别编码")
    private String warehouseCategoryCode;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "采购列表", required = true)
    List<PurchaseDetailsAddDTO> purchaseDetailsAddList;
    @ApiModelProperty(value = "入库列表", required = true)
    List<PurchaseStorageDetailsAddDTO> purchaseStorageDetailsAddList;

    public void insertHandle() {
        Optional<List<PurchaseDetailsAddDTO>> purchaseDetailsOpt = Optional.ofNullable(purchaseDetailsAddList).filter(CollectionUtils::isNotEmpty);
        Optional<List<PurchaseStorageDetailsAddDTO>> purchaseStorageDetailsOpt = Optional.ofNullable(purchaseStorageDetailsAddList).filter(CollectionUtils::isNotEmpty);
        purchaseDetailsOpt
                .ifPresent(x -> {
                    //计算采购数量总和
                    purchaseQuantity = x.stream().mapToInt(PurchaseDetailsAddDTO::getPurchaseQuantity).sum();
                });
        purchaseStorageDetailsOpt
                .ifPresent(x -> {
                    //计算已入库总和
                    quantityInStorageCreated = x.stream().mapToInt(PurchaseStorageDetailsAddDTO::getDeclareQty).sum();
                    //计算剩余需要采购的数量
                    remainingPurchaseQuantity = purchaseQuantity - quantityInStorageCreated;
                });
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
