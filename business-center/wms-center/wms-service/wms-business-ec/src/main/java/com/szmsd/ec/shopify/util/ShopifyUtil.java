package com.szmsd.ec.shopify.util;

import com.szmsd.ec.dto.ShopifyOrderDTO;
import com.szmsd.ec.shopify.config.ShopifyConfig;
import com.szmsd.ec.shopify.domain.order.*;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @FileName ShopifyUtil.java
 * @Description ----------功能描述---------
 * @Date 2021-04-15 11:46
 * @Author hyx
 * @Version 1.0
 */
public class ShopifyUtil {
    /**
     * 获取shopify配送地址
     * @param shopifyOrderDTO
     * @param shippingAddress
     * @author hyx
     * @date 2021-04-15 11:50
     * @throws
     * @return void
     */
    public static void getShippingAddress(ShopifyOrderDTO shopifyOrderDTO, ShopifyShippingAddress shippingAddress){
        shopifyOrderDTO.setShippingAddressAddress1(shippingAddress.getAddress1());
        shopifyOrderDTO.setShippingAddressAddress2(shippingAddress.getAddress2());
        shopifyOrderDTO.setShippingAddressName(shippingAddress.getName());
        shopifyOrderDTO.setShippingAddressFirstName(shippingAddress.getFirst_name());
        shopifyOrderDTO.setShippingAddressLastName(shippingAddress.getLast_name());
        shopifyOrderDTO.setShippingAddressLatitude(shippingAddress.getLatitude());
        shopifyOrderDTO.setShippingAddressLongitude(shippingAddress.getLongitude());
        shopifyOrderDTO.setShippingAddressCity(shippingAddress.getCity());
        shopifyOrderDTO.setShippingAddressCompany(shippingAddress.getCompany());
        shopifyOrderDTO.setShippingAddressPhone(shippingAddress.getPhone());
        shopifyOrderDTO.setShippingAddressProvince(shippingAddress.getProvince());
        shopifyOrderDTO.setShippingAddressProvinceCode(shippingAddress.getProvince_code());
        shopifyOrderDTO.setShippingAddressCountry(shippingAddress.getCountry());
        shopifyOrderDTO.setShippingAddressCountryCode(shippingAddress.getCountry_code());
        shopifyOrderDTO.setShippingAddressZip(shippingAddress.getZip());
    }
    /**
     * 获取shopify账单地址
     * @param shopifyOrderDTO
     * @param billingAddress
     * @author hyx
     * @date 2021-04-15 11:50
     * @throws
     * @return void
     */
    public static void getBillingAddress(ShopifyOrderDTO shopifyOrderDTO, BillingAddress billingAddress){
        shopifyOrderDTO.setBillingAddressAddress1(billingAddress.getAddress1());
        shopifyOrderDTO.setBillingAddressAddress2(billingAddress.getAddress2());
        shopifyOrderDTO.setBillingAddressName(billingAddress.getName());
        shopifyOrderDTO.setBillingAddressFirstName(billingAddress.getFirst_name());
        shopifyOrderDTO.setBillingAddressLastName(billingAddress.getLast_name());
        shopifyOrderDTO.setBillingAddressLatitude(billingAddress.getLatitude());
        shopifyOrderDTO.setBillingAddressLongitude(billingAddress.getLongitude());
        shopifyOrderDTO.setBillingAddressCity(billingAddress.getCity());
        shopifyOrderDTO.setBillingAddressCompany(billingAddress.getCompany());
        shopifyOrderDTO.setBillingAddressPhone(billingAddress.getPhone());
        shopifyOrderDTO.setBillingAddressProvince(billingAddress.getProvince());
        shopifyOrderDTO.setBillingAddressProvinceCode(billingAddress.getProvince_code());
        shopifyOrderDTO.setBillingAddressCountry(billingAddress.getCountry());
        shopifyOrderDTO.setBillingAddressCountryCode(billingAddress.getCountry_code());
        shopifyOrderDTO.setBillingAddressZip(billingAddress.getZip());
    }
    /**
     * 获取shopify默认配送地址
     * @param shopifyOrderDTO
     * @param defaultAddress
     * @author hyx
     * @date 2021-04-15 11:50
     * @throws
     * @return void
     */
    public static void getDefaultAddress(ShopifyOrderDTO shopifyOrderDTO, DefaultAddress defaultAddress){
        shopifyOrderDTO.setDefaultAddressAddress1(defaultAddress.getAddress1());
        shopifyOrderDTO.setDefaultAddressAddress2(defaultAddress.getAddress2());
        shopifyOrderDTO.setDefaultAddressName(defaultAddress.getName());
        shopifyOrderDTO.setDefaultAddressFirstName(defaultAddress.getFirst_name());
        shopifyOrderDTO.setDefaultAddressLastName(defaultAddress.getLast_name());
        shopifyOrderDTO.setDefaultAddressCity(defaultAddress.getCity());
        shopifyOrderDTO.setDefaultAddressCompany(defaultAddress.getCompany());
        shopifyOrderDTO.setDefaultAddressPhone(defaultAddress.getPhone());
        shopifyOrderDTO.setDefaultAddressProvince(defaultAddress.getProvince());
        shopifyOrderDTO.setDefaultAddressProvinceCode(defaultAddress.getProvince_code());
        shopifyOrderDTO.setDefaultAddressCountry(defaultAddress.getCountry());
        shopifyOrderDTO.setDefaultAddressCountryCode(defaultAddress.getCountry_code());
        shopifyOrderDTO.setDefaultAddressZip(defaultAddress.getZip());
    }
    /**
     * 处理费用
     * @param shopifyOrderDTO
     * @param order
     * @author hyx
     * @date 2021-04-15 11:52
     * @throws
     * @return void
     */
    public static void getTotalPrice(ShopifyOrderDTO shopifyOrderDTO, ShopifyOrders order){
        //处理费用
        shopifyOrderDTO.setTotalDiscounts(changeTotal(order.getTotal_discounts()));
        shopifyOrderDTO.setTotalLineItemsPrice(changeTotal(order.getTotal_line_items_price()));
        shopifyOrderDTO.setTotalOutstanding(changeTotal(order.getTotal_outstanding()));
        shopifyOrderDTO.setTotalPrice(changeTotal(order.getTotal_price()));
        shopifyOrderDTO.setTotalPriceUsd(changeTotal(order.getTotal_price_usd()));
        shopifyOrderDTO.setTotalTax(changeTotal(order.getTotal_tax()));
        shopifyOrderDTO.setTotalTipReceived(changeTotal(order.getTotal_tip_received()));
        shopifyOrderDTO.setTotalWeight(order.getTotal_weight());

        shopifyOrderDTO.setCurrentTotalDiscounts(changeTotal(order.getCurrent_total_discounts()));
        shopifyOrderDTO.setCurrentSubtotalPrice(changeTotal(order.getCurrent_subtotal_price()));
        shopifyOrderDTO.setCurrentTotalDutiesSet(changeTotal(order.getCurrent_total_duties_set()));
        shopifyOrderDTO.setCurrentTotalPrice(changeTotal(order.getCurrent_total_price()));
        shopifyOrderDTO.setCurrentTotalTax(changeTotal(order.getCurrent_total_tax()));
    }
    /**
     * 费用类型转换
     * @param str
     * @author hyx
     * @date 2021-04-15 11:53
     * @throws
     * @return java.lang.Double
     */
    public static Double changeTotal(String str){
        if (StringUtils.isEmpty(str)){
            return 0.0;
        }
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    /**
     * 获取shopify支付信息
     * @param shopifyOrderDTO
     * @param paymentDetail
     * @author hyx
     * @date 2021-04-15 11:54
     * @throws
     * @return void
     */
    public static void  getPaymentDetail(ShopifyOrderDTO shopifyOrderDTO, PaymentDetails paymentDetail){
        shopifyOrderDTO.setCreditCardBin(paymentDetail.getCredit_card_bin());
        shopifyOrderDTO.setCreditCardCompany(paymentDetail.getCredit_card_company());
        shopifyOrderDTO.setCreditCardNumber(paymentDetail.getCredit_card_number());
        shopifyOrderDTO.setAvsResultCode(paymentDetail.getAvs_result_code());
        shopifyOrderDTO.setCvvResultCode(paymentDetail.getCvv_result_code());
    }
    /**
     * 格式化时间格式
     * @param shopifyOrderDTO
     * @param order
     * @author hyx
     * @date 2021-04-15 14:00
     * @throws
     * @return void
     */
    public static void getTime(ShopifyOrderDTO shopifyOrderDTO,ShopifyOrders order){
        try {
            shopifyOrderDTO.setCreatedAt(parse(order.getCreated_at(), ShopifyConfig.TIME_FORMAT_STR));
            shopifyOrderDTO.setClosedAt(parse(order.getClosed_at(),ShopifyConfig.TIME_FORMAT_STR));
            shopifyOrderDTO.setCancelledAt(parse(order.getCancelled_at(),ShopifyConfig.TIME_FORMAT_STR));
            shopifyOrderDTO.setProcessedAt(parse(order.getProcessed_at(),ShopifyConfig.TIME_FORMAT_STR));
            shopifyOrderDTO.setUpdatedAt(parse(order.getUpdated_at(),ShopifyConfig.TIME_FORMAT_STR));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Date parse(String str,String form){
        try {
            if (StringUtils.isEmpty(str)){
                return null;
            }
            SimpleDateFormat formatter = new SimpleDateFormat(form);
            Date date = formatter.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}