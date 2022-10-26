package com.szmsd.delivery.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.BasTrackingPush;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.mapper.BasTrackingPushMapper;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.service.impl.DelOutboundServiceImpl;
import com.szmsd.http.api.feign.HtpOutboundFeignService;
import com.szmsd.http.api.service.IHtpRmiClientService;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.ShipmentTrackingChangeRequestDto;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.http.vo.ResponseVO;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.*;

public class DeleTrackingPushJob extends QuartzJobBean {

    private Logger logger = LoggerFactory.getLogger(DeleTrackingPushJob.class);
    @Autowired
    private HtpOutboundFeignService htpOutboundFeignService;


    @Autowired
    private IHtpRmiClientService htpRmiClientService;

    @Autowired
    private DelOutboundMapper delOutboundMapper;

    @Autowired
    private BasTrackingPushMapper basTrackingPushMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

         List<BasTrackingPush>  list=basTrackingPushMapper.selectByPrimaryKey();
         if (list.size()>0) {
             list.forEach(s -> {

                 ShipmentTrackingChangeRequestDto shipmentTrackingChangeRequestDto = new ShipmentTrackingChangeRequestDto();
                 BeanUtils.copyProperties(s, shipmentTrackingChangeRequestDto);
                 R<ResponseVO> r = htpOutboundFeignService.shipmentTracking(shipmentTrackingChangeRequestDto);

                 manualTrackingYees(shipmentTrackingChangeRequestDto.getOrderNo());
             });
         }

    }

    public void manualTrackingYees(String orderNo) {

        DelOutboundListQueryDto delOutboundListQueryDto=delOutboundMapper.pageLists(orderNo);
        TraYees(delOutboundListQueryDto);


    }
    public void TraYees(DelOutboundListQueryDto delOutboundListQueryDto){
        boolean success = false;
        String responseBody;
        try {
            Map<String, Object> requestBodyMap = new HashMap<>();
            List<Map<String, Object>> shipments = new ArrayList<>();
            Map<String, Object> shipment = new HashMap<>();
            shipment.put("trackingNo", delOutboundListQueryDto.getTrackingNo());
            shipment.put("carrierCode", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("logisticsServiceProvider", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("logisticsServiceName", delOutboundListQueryDto.getLogisticsProviderCode());
            shipment.put("platformCode", "DM");
            shipment.put("shopName", "");
            Date createTime = delOutboundListQueryDto.getCreateTime();
            if (null != createTime) {
                shipment.put("OrdersOn", DateFormatUtils.format(createTime, "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"));
            }
            shipment.put("paymentTime", "");
            shipment.put("shippingOn", "");
            List<String> searchTags = new ArrayList<>();
            searchTags.add("");
            searchTags.add("");
            shipment.put("searchTags", searchTags);
            shipment.put("orderNo", delOutboundListQueryDto.getOrderNo());
            Map<String, Object> senderAddress = new HashMap<>();
            senderAddress.put("country", delOutboundListQueryDto.getCountry());
            senderAddress.put("province", delOutboundListQueryDto.getStateOrProvince());
            senderAddress.put("city", delOutboundListQueryDto.getCity());
            senderAddress.put("postcode", delOutboundListQueryDto.getPostCode());
            senderAddress.put("street1", delOutboundListQueryDto.getStreet1());
            senderAddress.put("street2", delOutboundListQueryDto.getStreet2());
            senderAddress.put("street3", delOutboundListQueryDto.getStreet3());
            shipment.put("senderAddress", senderAddress);
            Map<String, Object> destinationAddress = new HashMap<>();
            destinationAddress.put("country", delOutboundListQueryDto.getCountry());
            destinationAddress.put("province", delOutboundListQueryDto.getStateOrProvince());
            destinationAddress.put("city", delOutboundListQueryDto.getCity());
            destinationAddress.put("postcode", delOutboundListQueryDto.getPostCode());
            destinationAddress.put("street1", delOutboundListQueryDto.getStreet1());
            destinationAddress.put("street2",delOutboundListQueryDto.getStreet2());
            destinationAddress.put("street3", delOutboundListQueryDto.getStreet3());
            shipment.put("destinationAddress", destinationAddress);
            Map<String, Object> recipientInfo = new HashMap<>();
            recipientInfo.put("recipient", delOutboundListQueryDto.getConsignee());
            recipientInfo.put("phoneNumber", delOutboundListQueryDto.getPhoneNo());
            recipientInfo.put("email", "");
            shipment.put("recipientInfo", recipientInfo);
            Map<String, Object> customFieldInfo = new HashMap<>();
            customFieldInfo.put("fieldOne", delOutboundListQueryDto.getOrderNo());
            customFieldInfo.put("fieldTwo", "");
            customFieldInfo.put("fieldThree", "");
            shipment.put("customFieldInfo", customFieldInfo);
            shipments.add(shipment);
            requestBodyMap.put("shipments", shipments);
            HttpRequestDto httpRequestDto = new HttpRequestDto();
            httpRequestDto.setMethod(HttpMethod.POST);
            String url = DomainEnum.TrackingYeeDomain.wrapper("/tracking/v1/shipments");
            httpRequestDto.setUri(url);
            httpRequestDto.setBody(requestBodyMap);
            HttpResponseVO httpResponseVO = htpRmiClientService.rmi(httpRequestDto);
            if (200 == httpResponseVO.getStatus() ||
                    201 == httpResponseVO.getStatus()) {
                success = true;
            }
            responseBody = (String) httpResponseVO.getBody();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseBody = e.getMessage();
            if (null == responseBody) {
                responseBody = "请求失败";
            }
        }
        // 请求成功，解析响应报文
        if (success) {
            try {
                // 解析响应报文，获取响应参数信息
                JSONObject jsonObject = JSON.parseObject(responseBody);
                // 判断状态是否为OK
                if ("OK".equals(jsonObject.getString("status"))) {
                    // 判断结果明细是不是成功的
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (1 != data.getIntValue("successNumber")) {
                        // 返回的成功数量不是1，判定为异常
                        success = false;
                        // 获取异常信息
                        int failNumber = data.getIntValue("failNumber");
                        if (failNumber > 0) {
                            JSONArray failImportRowResults = data.getJSONArray("failImportRowResults");
                            JSONObject failImportRowResult = failImportRowResults.getJSONObject(0);
                            JSONObject errorInfo = failImportRowResult.getJSONObject("errorInfo");
                            String errorCode = errorInfo.getString("errorCode");
                            String errorMessage = errorInfo.getString("errorMessage");
                            //throw new CommonException("500", "[" + errorCode + "]" + errorMessage);
                        }
                    }
                }
                basTrackingPushMapper.deleteByPrimaryKey(delOutboundListQueryDto.getOrderNo());

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (e instanceof CommonException) {
                    //throw e;
                }
                // 解析失败，判定为异常
                success = false;
            }
        }
        if (!success) {
            //throw new CommonException("500", "创建TrackingYee失败");
        }
    }
}
