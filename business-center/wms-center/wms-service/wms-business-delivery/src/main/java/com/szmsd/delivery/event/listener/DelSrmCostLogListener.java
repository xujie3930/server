package com.szmsd.delivery.event.listener;

import com.alibaba.fastjson.JSON;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.config.TyRequestConfig;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelSrmCostLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.enums.DelSrmCostLogEnum;
import com.szmsd.delivery.event.DelSrmCostLogEvent;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.IDelSrmCostLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class DelSrmCostLogListener {

    @Autowired
    private TyRequestConfig tyRequestConfig;
    @Autowired
    private IDelSrmCostLogService delSrmCostLogService;
    @Autowired
    private IDelOutboundService delOutboundService;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_SRM_SAVE)
    @EventListener
    public void onApplicationEvent(DelSrmCostLogEvent event) {
        DelSrmCostLog delSrmCostLog = (DelSrmCostLog) event.getSource();
        delSrmCostLog.setState(DelCk1RequestLogConstant.State.WAIT.name());
        delSrmCostLog.setNextRetryTime(new Date());
        String type = delSrmCostLog.getType();
        // 填充请求体的内容
        String orderNo = delSrmCostLog.getOrderNo();
        DelOutbound delOutbound = this.delOutboundService.getByOrderNo(orderNo);
        if (null == delOutbound) {
            return;
        }
        Map<String, Object> delSrmCostLogRequest = new HashMap<>();
        delSrmCostLogRequest.put("order_no", orderNo);
        delSrmCostLog.setRequestBody(JSON.toJSONString(delSrmCostLogRequest));
       /* ApiValue apiValue = tyRequestConfig.getApi(type);
        String url = DomainEnum.TrackingYeeDomain.wrapper(apiValue.getUrl());
        delSrmCostLog.setUrl(url);
        delSrmCostLog.setMethod(apiValue.getHttpMethod().name());*/
        this.delSrmCostLogService.save(delSrmCostLog);
    }
}
