package com.szmsd.http.config.inner;

import com.szmsd.http.config.inner.url.*;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:22
 */
public class UrlGroupConfig {

    private WmsUrlConfig wms;
    private SaaSPricedProductUrlConfig pricedProduct2;
    private SaaSCarrierServiceAdminUrlConfig carrierService;
    private SaaSProductRemoteAreaUrlConfig productRemoteArea;
    private ThirdPaymentUrlConfig thirdPayment;
    private SrmUrlConfig srm;


    private SaaSPricedUrlConfig priced;

    public WmsUrlConfig getWms() {
        return wms;
    }

    public void setWms(WmsUrlConfig wms) {
        this.wms = wms;
    }

    public SaaSPricedProductUrlConfig getPricedProduct() {
        return pricedProduct2;
    }

    public void setPricedProduct(SaaSPricedProductUrlConfig pricedProduct) {
        this.pricedProduct2 = pricedProduct;
    }

    public SaaSCarrierServiceAdminUrlConfig getCarrierService() {
        return carrierService;
    }

    public void setCarrierService(SaaSCarrierServiceAdminUrlConfig carrierService) {
        this.carrierService = carrierService;
    }

    public SaaSProductRemoteAreaUrlConfig getProductRemoteArea() {
        return productRemoteArea;
    }

    public void setProductRemoteArea(SaaSProductRemoteAreaUrlConfig productRemoteArea) {
        this.productRemoteArea = productRemoteArea;
    }

    public ThirdPaymentUrlConfig getThirdPayment() {
        return thirdPayment;
    }

    public void setThirdPayment(ThirdPaymentUrlConfig thirdPayment) {
        this.thirdPayment = thirdPayment;
    }

    public SrmUrlConfig getSrm() {
        return srm;
    }

    public void setSrm(SrmUrlConfig srm) {
        this.srm = srm;
    }


    public SaaSPricedUrlConfig getPriced() {
        return priced;
    }

    public void setPriced(SaaSPricedUrlConfig priced) {
        this.priced = priced;
    }
}
