package com.szmsd.track.event.listener;

import com.alibaba.fastjson.JSON;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.vo.DelOutboundAddressVO;
import com.szmsd.delivery.vo.DelOutboundVO;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.track.config.ApiValue;
import com.szmsd.track.config.ThreadPoolExecutorConfiguration;
import com.szmsd.track.config.TyRequestConfig;
import com.szmsd.track.domain.TrackTyRequestLog;
import com.szmsd.track.enums.TrackTyRequestLogConstant;
import com.szmsd.track.event.TrackTyRequestLogEvent;
import com.szmsd.track.service.ITrackTyRequestLogService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TrackTyRequestLogListener {

    @Autowired
    private TyRequestConfig tyRequestConfig;
    @Autowired
    private ITrackTyRequestLogService delTyRequestLogService;
    @Autowired
    private DelOutboundFeignService delOutboundService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_TY_SAVE)
    @EventListener
    public void onApplicationEvent(TrackTyRequestLogEvent event) {
        TrackTyRequestLog tyRequestLog = (TrackTyRequestLog) event.getSource();
        tyRequestLog.setState(DelCk1RequestLogConstant.State.WAIT.name());
        tyRequestLog.setNextRetryTime(new Date());
        String type = tyRequestLog.getType();
        // 填充请求体的内容
        if (TrackTyRequestLogConstant.Type.shipments.name().equals(type)) {
            String orderNo = tyRequestLog.getOrderNo();
            R<DelOutboundVO> delOutboundRs = this.delOutboundService.getInfoByOrderNo(orderNo);

            if(delOutboundRs == null || delOutboundRs.getCode() != Constants.SUCCESS){
                return;
            }

            DelOutboundVO delOutbound = delOutboundRs.getData();

            if (null == delOutbound) {
                return;
            }

            DelOutboundAddressVO delOutboundAddressVO = delOutbound.getAddress();

            BasWarehouse basWarehouse = this.basWarehouseClientService.queryByWarehouseCode(delOutbound.getWarehouseCode());
            // 接口文档
            // 生产环境
            // https://developer.trackingyee.com/documentCenter/list?index=2&id=6272629d-5e51-4f88-94cd-ab0201248f82&type=api
            // 开发环境
            // https://developer.360bbt.com/documentCenter/list?index=2&id=6272629d-5e51-4f88-94cd-ab0201248f82&type=api
            Map<String, Object> requestBodyMap = new HashMap<>();
            List<Map<String, Object>> shipments = new ArrayList<>();
            Map<String, Object> shipment = new HashMap<>();
            shipment.put("trackingNo", delOutbound.getTrackingNo());
            shipment.put("carrierCode", delOutbound.getLogisticsProviderCode());
            shipment.put("logisticsServiceProvider", delOutbound.getLogisticsProviderCode());
            shipment.put("logisticsServiceName", delOutbound.getLogisticsProviderCode());
            shipment.put("platformCode", "DM");
            shipment.put("shopName", "");
            Date createTime = delOutbound.getCreateTime();
            if (null != createTime) {
                shipment.put("OrdersOn", DateFormatUtils.format(createTime, "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));
            }
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
            if (null != delOutboundAddressVO) {
                destinationAddress.put("country", delOutboundAddressVO.getCountryCode());
                destinationAddress.put("province", delOutboundAddressVO.getStateOrProvince());
                destinationAddress.put("city", delOutboundAddressVO.getCity());
                destinationAddress.put("postcode", delOutboundAddressVO.getPostCode());
                destinationAddress.put("street1", delOutboundAddressVO.getStreet1());
                destinationAddress.put("street2", delOutboundAddressVO.getStreet2());
                destinationAddress.put("street3", delOutboundAddressVO.getStreet3());
            }
            shipment.put("destinationAddress", destinationAddress);
            Map<String, Object> recipientInfo = new HashMap<>();
            if (null != delOutboundAddressVO) {
                recipientInfo.put("recipient", delOutboundAddressVO.getConsignee());
                recipientInfo.put("phoneNumber", delOutboundAddressVO.getPhoneNo());
                recipientInfo.put("email", delOutboundAddressVO.getEmail());
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
