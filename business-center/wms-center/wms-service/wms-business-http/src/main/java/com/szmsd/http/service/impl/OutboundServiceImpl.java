package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IOutboundService;
import com.szmsd.http.service.http.WmsRequest;
import com.szmsd.http.util.HttpResponseVOUtils;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:23
 */
@Slf4j
@Service
public class OutboundServiceImpl extends WmsRequest implements IOutboundService {

    @Value("${directExpressToken}")
    private String directExpressToken;

    public OutboundServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public CreateShipmentResponseVO shipmentCreate(CreateShipmentRequestDto dto) {
        log.info("shipmentCreate：{}", JSONObject.toJSONString(dto));
        String text = httpPost(dto.getWarehouseCode(), "outbound.create", dto);
        try {
            return JSON.parseObject(text, CreateShipmentResponseVO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            CreateShipmentResponseVO createShipmentResponseVO = new CreateShipmentResponseVO();
            createShipmentResponseVO.setSuccess(false);
            createShipmentResponseVO.setMessage(text);
            return createShipmentResponseVO;
        }
    }

    @Override
    public ResponseVO shipmentDelete(ShipmentCancelRequestDto dto) {
        String text = httpDelete(dto.getWarehouseCode(), "outbound.cancel", dto);
        return ResponseVO.build(text);
    }

    @Override
    public ResponseVO shipmentTracking(ShipmentTrackingChangeRequestDto dto) {
        String text = httpPut(dto.getWarehouseCode(), "outbound.tracking", dto);
        return ResponseVO.build(text);
    }

    @Override
    public ResponseVO shipmentLabel(ShipmentLabelChangeRequestDto dto) {
        String text = httpPut(dto.getWarehouseCode(), "outbound.label", dto);
        return ResponseVO.build(text);
    }

    @Override
    public ResponseVO shipmentShipping(ShipmentUpdateRequestDto dto) {
        String text = httpPut(dto.getWarehouseCode(), "outbound.shipping", dto);
        return ResponseVO.build(text);
    }

    @Override
    public ResponseVO shipmentMultiboxrelation(ShipmentMultiboxrelationRequestDto dto) {
        String text = httpPut(dto.getWarehouseCode(), "outbound.multiboxrelation", dto);
        return ResponseVO.build(text);
    }

    @Override
    public R shipmentBoxtransfer(BulkOrderRequestDto dto) {
        return HttpResponseVOUtils.transformation(httpPostBody(null, "outbound.boxtransfer", dto));
    }

    @Override
    public R<DirectExpressOrderApiDTO> getDirectExpressOrder(String orderNo) {

        String url = "https://openapi.chukou1.cn/v1/directExpressOrders/"+orderNo+"/status";

        Map<String, String> headerMap = new HashMap<>();

        headerMap.put("Authorization", "Bearer "+directExpressToken);

        log.info("directExpressOrders url ：{}",url);
        log.info("directExpressOrders token : {}",directExpressToken);

        HttpResponseBody httpResponseBody = HttpClientHelper.httpGet(url, null, headerMap);

        String body = httpResponseBody.getBody();

        if(StringUtils.isNotEmpty(body)) {
            DirectExpressOrderApiDTO directExpressOrderApiDTO = JSON.parseObject(body, new TypeReference<DirectExpressOrderApiDTO>() {
            }.getType());
            return R.ok(directExpressOrderApiDTO);
        }else{
            log.error("异常:{}"+JSON.toJSONString(body));
            return R.failed("获取数据异常");
        }
    }

    @Override
    public R<Integer> updateDirectExpressOrderWeight(DirectExpressOrderWeightDto dto) {

        String url = "https://openapi.chukou1.cn/v1/directExpressOrders";

        Map<String, String> headerMap = new HashMap<>();

        headerMap.put("Authorization", "Bearer "+directExpressToken);

        log.info("updateDirectExpressOrderWeight url ：{}",url);
        log.info("updateDirectExpressOrderWeight token : {}",directExpressToken);

        HttpResponseBody httpResponseBody = HttpClientHelper.httpPut(url, JSON.toJSONString(dto), headerMap);

        log.info("updateDirectExpressOrderWeight httpResponseBody : {}",JSON.toJSONString(httpResponseBody));
        if(httpResponseBody.getStatus() == Constants.SUCCESS) {
            return R.ok();
        }else{
            return R.failed("提交数据异常");
        }
    }
}
