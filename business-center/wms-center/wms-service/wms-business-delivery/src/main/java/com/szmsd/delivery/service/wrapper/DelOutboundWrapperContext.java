package com.szmsd.delivery.service.wrapper;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 15:07
 */
public class DelOutboundWrapperContext implements ApplicationContext {

    private DelOutbound delOutbound;
    private DelOutboundAddress address;
    private List<DelOutboundDetail> detailList;
    private BasWarehouse warehouse;
    private BasRegionSelectListVO country;
    private List<BaseProduct> productList;
    private List<BasePacking> packingList;
    // 失败了是否推送发货指令，默认false不推送
    private boolean shipmentShipping;

    public DelOutboundWrapperContext() {
    }

    public DelOutboundWrapperContext(DelOutbound delOutbound, DelOutboundAddress address, List<DelOutboundDetail> detailList, BasWarehouse warehouse, BasRegionSelectListVO country) {
        this();
        this.delOutbound = delOutbound;
        this.address = address;
        this.detailList = detailList;
        this.warehouse = warehouse;
        this.country = country;
    }

    public DelOutboundWrapperContext(DelOutbound delOutbound, DelOutboundAddress address, List<DelOutboundDetail> detailList, BasWarehouse warehouse, BasRegionSelectListVO country, List<BaseProduct> productList, List<BasePacking> packingList) {
        this(delOutbound, address, detailList, warehouse, country);
        this.productList = productList;
        this.packingList = packingList;
    }

    public DelOutbound getDelOutbound() {
        return delOutbound;
    }

    public void setDelOutbound(DelOutbound delOutbound) {
        this.delOutbound = delOutbound;
    }

    public DelOutboundAddress getAddress() {
        return address;
    }

    public void setAddress(DelOutboundAddress address) {
        this.address = address;
    }

    public List<DelOutboundDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DelOutboundDetail> detailList) {
        this.detailList = detailList;
    }

    public BasWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(BasWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public BasRegionSelectListVO getCountry() {
        return country;
    }

    public void setCountry(BasRegionSelectListVO country) {
        this.country = country;
    }

    public List<BaseProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<BaseProduct> productList) {
        this.productList = productList;
    }

    public List<BasePacking> getPackingList() {
        return packingList;
    }

    public void setPackingList(List<BasePacking> packingList) {
        this.packingList = packingList;
    }

    public boolean isShipmentShipping() {
        return shipmentShipping;
    }

    public void setShipmentShipping(boolean shipmentShipping) {
        this.shipmentShipping = shipmentShipping;
    }

}
