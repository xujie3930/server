package com.szmsd.delivery.event.listener;

import com.alibaba.fastjson.JSON;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.delivery.config.ApiValue;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.config.TyRequestConfig;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelTyRequestLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.enums.DelTyRequestLogConstant;
import com.szmsd.delivery.event.DelTyRequestLogEvent;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.IDelTyRequestLogService;
import com.szmsd.http.enums.DomainEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DelTyRequestLogListener {

    @Autowired
    private TyRequestConfig tyRequestConfig;
    @Autowired
    private IDelTyRequestLogService delTyRequestLogService;
    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_TY_SAVE)
    @EventListener
    public void onApplicationEvent(DelTyRequestLogEvent event) {
        DelTyRequestLog tyRequestLog = (DelTyRequestLog) event.getSource();
        tyRequestLog.setState(DelCk1RequestLogConstant.State.WAIT.name());
        tyRequestLog.setNextRetryTime(new Date());
        String type = tyRequestLog.getType();
        // 填充请求体的内容
        if (DelTyRequestLogConstant.Type.shipments.name().equals(type)) {
            String orderNo = tyRequestLog.getOrderNo();
            DelOutbound delOutbound = this.delOutboundService.getByOrderNo(orderNo);
            if (null == delOutbound) {
                return;
            }
            DelOutboundAddress delOutboundAddress = this.delOutboundAddressService.getByOrderNo(orderNo);
            BasWarehouse basWarehouse = this.basWarehouseClientService.queryByWarehouseCode(delOutbound.getWarehouseCode());
            // 接口文档
            // https://developer.trackingyee.com/documentCenter/list?index=2&id=6272629d-5e51-4f88-94cd-ab0201248f82&type=api
            Map<String, Object> requestBodyMap = new HashMap<>();
            List<Map<String, Object>> shipments = new ArrayList<>();
            Map<String, Object> shipment = new HashMap<>();
            shipment.put("trackingNo", delOutbound.getTrackingNo());
            shipment.put("carrierCode", delOutbound.getLogisticsProviderCode());
            shipment.put("logisticsServiceProvider", delOutbound.getLogisticsProviderCode());
            shipment.put("logisticsServiceName", delOutbound.getLogisticsProviderCode());
            shipment.put("platformCode", "DM");
            shipment.put("shopName", "");
            shipment.put("OrdersOn", delOutbound.getCreateTime());
            shipment.put("paymentTime", "");
            shipment.put("shippingOn", "");
            List<String> searchTags = new ArrayList<>();
            searchTags.add(delOutbound.getSellerCode());
            searchTags.add(delOutbound.getOrderNo());
            shipment.put("searchTags", searchTags);
            shipment.put("orderNo", delOutbound.getOrderNo());
            Map<String, Object> senderAddress = new HashMap<>();
            if (null != basWarehouse) {
                senderAddress.put("country", basWarehouse.getCountryCode());
                senderAddress.put("province", basWarehouse.getProvince());
                senderAddress.put("city", basWarehouse.getCity());
                senderAddress.put("postcode", basWarehouse.getPostcode());
                senderAddress.put("street1", basWarehouse.getStreet1());
                senderAddress.put("street2", basWarehouse.getStreet2());
                senderAddress.put("street3", "");
            }
            shipment.put("senderAddress", senderAddress);
            Map<String, Object> destinationAddress = new HashMap<>();
            if (null != delOutboundAddress) {
                destinationAddress.put("country", delOutboundAddress.getCountryCode());
                destinationAddress.put("province", delOutboundAddress.getStateOrProvince());
                destinationAddress.put("city", delOutboundAddress.getCity());
                destinationAddress.put("postcode", delOutboundAddress.getPostCode());
                destinationAddress.put("street1", delOutboundAddress.getStreet1());
                destinationAddress.put("street2", delOutboundAddress.getStreet2());
                destinationAddress.put("street3", delOutboundAddress.getStreet3());
            }
            shipment.put("destinationAddress", destinationAddress);
            Map<String, Object> recipientInfo = new HashMap<>();
            if (null != delOutboundAddress) {
                recipientInfo.put("recipient", delOutboundAddress.getConsignee());
                recipientInfo.put("phoneNumber", delOutboundAddress.getPhoneNo());
                recipientInfo.put("email", delOutboundAddress.getEmail());
            }
            shipment.put("recipientInfo", recipientInfo);
            Map<String, Object> customFieldInfo = new HashMap<>();
            customFieldInfo.put("fieldOne", delOutbound.getOrderNo());
            customFieldInfo.put("fieldTwo", "");
            customFieldInfo.put("fieldThree", "");
            shipment.put("customFieldInfo", customFieldInfo);
            shipments.add(shipment);
            requestBodyMap.put("shipments", shipments);
            tyRequestLog.setRequestBody(JSON.toJSONString(requestBodyMap));
        }
        ApiValue apiValue = tyRequestConfig.getApi(type);
        String url = DomainEnum.TrackingYeeDomain.wrapper(apiValue.getUrl());
        tyRequestLog.setUrl(url);
        tyRequestLog.setMethod(apiValue.getHttpMethod().name());
        this.delTyRequestLogService.save(tyRequestLog);
    }
}
