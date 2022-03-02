package com.szmsd.http.config.inner;

import com.szmsd.http.config.inner.api.*;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:14
 */
public class DefaultApiConfig {

    private WmsApiConfig wms;
    private SaaSPricedProductApiConfig pricedProduct;
    private SaaSCarrierServiceAdminApiConfig carrierService;
    private SaaSProductRemoteAreaApiConfig productRemoteArea;
    private ThirdPaymentApiConfig thirdPayment;
    private SrmApiConfig srm;

    public WmsApiConfig getWms() {
        return wms;
    }

    public void setWms(WmsApiConfig wms) {
        this.wms = wms;
    }

    public SaaSPricedProductApiConfig getPricedProduct() {
        return pricedProduct;
    }

    public void setPricedProduct(SaaSPricedProductApiConfig pricedProduct) {
        this.pricedProduct = pricedProduct;
    }

    public SaaSCarrierServiceAdminApiConfig getCarrierService() {
        return carrierService;
    }

    public void setCarrierService(SaaSCarrierServiceAdminApiConfig carrierService) {
        this.carrierService = carrierService;
    }

    public SaaSProductRemoteAreaApiConfig getProductRemoteArea() {
        return productRemoteArea;
    }

    public void setProductRemoteArea(SaaSProductRemoteAreaApiConfig productRemoteArea) {
        this.productRemoteArea = productRemoteArea;
    }

    public ThirdPaymentApiConfig getThirdPayment() {
        return thirdPayment;
    }

    public void setThirdPayment(ThirdPaymentApiConfig thirdPayment) {
        this.thirdPayment = thirdPayment;
    }

    public SrmApiConfig getSrm() {
        return srm;
    }

    public void setSrm(SrmApiConfig srm) {
        this.srm = srm;
    }
}
