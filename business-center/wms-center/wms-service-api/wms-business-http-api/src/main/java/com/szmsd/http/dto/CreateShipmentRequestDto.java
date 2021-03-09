package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 10:49
 */
@ApiModel(value = "CreateShipmentRequestDto", description = "CreateShipmentRequestDto对象")
public class CreateShipmentRequestDto implements Serializable {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "卖家代码")
    private String sellerCode;

    @ApiModelProperty(value = "挂号（可以为空）")
    private String trackingNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "装箱规则（在发货规则相同的前提下，相同装箱规则代表可以一起装箱）可空")
    private String packingRule;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "参照单号")
    private String refOrderNo;

    @ApiModelProperty(value = "是否必须按要求装箱")
    private Boolean isPackingByRequired;

    @ApiModelProperty(value = "是否优先发货")
    private String isFirst;

    @ApiModelProperty(value = "出库后重新上架的新SKU编码")
    private String newSKU;

    @ApiModelProperty(value = "出库单地址")
    private ShipmentAddressDto address;

    @ApiModelProperty(value = "出库单明细")
    private List<ShipmentDetailInfoDto> details;

    @ApiModelProperty(value = "装箱要求")
    private PackingRequirementInfoDto packingRequirement;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public String getShipmentRule() {
        return shipmentRule;
    }

    public void setShipmentRule(String shipmentRule) {
        this.shipmentRule = shipmentRule;
    }

    public String getPackingRule() {
        return packingRule;
    }

    public void setPackingRule(String packingRule) {
        this.packingRule = packingRule;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRefOrderNo() {
        return refOrderNo;
    }

    public void setRefOrderNo(String refOrderNo) {
        this.refOrderNo = refOrderNo;
    }

    public Boolean getPackingByRequired() {
        return isPackingByRequired;
    }

    public void setPackingByRequired(Boolean packingByRequired) {
        isPackingByRequired = packingByRequired;
    }

    public String getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(String isFirst) {
        this.isFirst = isFirst;
    }

    public String getNewSKU() {
        return newSKU;
    }

    public void setNewSKU(String newSKU) {
        this.newSKU = newSKU;
    }

    public ShipmentAddressDto getAddress() {
        return address;
    }

    public void setAddress(ShipmentAddressDto address) {
        this.address = address;
    }

    public List<ShipmentDetailInfoDto> getDetails() {
        return details;
    }

    public void setDetails(List<ShipmentDetailInfoDto> details) {
        this.details = details;
    }

    public PackingRequirementInfoDto getPackingRequirement() {
        return packingRequirement;
    }

    public void setPackingRequirement(PackingRequirementInfoDto packingRequirement) {
        this.packingRequirement = packingRequirement;
    }
}
