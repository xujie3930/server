package com.szmsd.inventory.domain.vo;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


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
public class PurchaseInfoVO {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "采购单号")
    @Excel(name = "采购单号")
    private String purchaseNo;

    @ApiModelProperty(value = "目标仓库code")
    @Excel(name = "目标仓库code")
    private String warehouseCode;

    @ApiModelProperty(value = "目标仓库")
    @Excel(name = "目标仓库")
    private String warehouseName;

    @ApiModelProperty(value = "出库方式编码")
    @Excel(name = "出库方式编码")
    private String orderType;

    @ApiModelProperty(value = "出库方式名")
    @Excel(name = "出库方式名")
    private String orderTypeName;

    @ApiModelProperty(value = "送货方式")
    @Excel(name = "送货方式")
    private String deliveryWayName;
    @ApiModelProperty(value = "剩余的采购数量")
    @Excel(name = "剩余的采购数量")
    private Integer remainingPurchaseQuantity;
    @ApiModelProperty(value = "送货方式编码")
    @Excel(name = "送货方式编码")
    private String deliveryWay;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "出库单号")
    @Excel(name = "出库单号")
    private String orderNo;
    @ApiModelProperty(value = "采购单列表")
    private List<PurchaseInfoDetailVO> purchaseInfoDetailList;
    @ApiModelProperty(value = "预入库列表")
    private List<PurchaseStorageDetailsVO> purchaseStorageDetailsList;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
