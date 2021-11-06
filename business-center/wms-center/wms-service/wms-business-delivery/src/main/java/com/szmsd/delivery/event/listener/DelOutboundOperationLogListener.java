package com.szmsd.delivery.event.listener;

import com.szmsd.delivery.domain.OperationLog;
import com.szmsd.delivery.event.DelOutboundOperationLogEvent;
import com.szmsd.delivery.event.OperationLogEnum;
import com.szmsd.delivery.service.IOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-06-22 16:30
 */
@Component
public class DelOutboundOperationLogListener {

    @Autowired
    private IOperationLogService operationLogService;

    @Async
    @EventListener
    public void onApplicationEvent(DelOutboundOperationLogEvent event) {
        Object source = event.getSource();
        OperationLogEnum operationLogEnum = event.getOperationLogEnum();
        String tid = event.getTid();
        String userCode = event.getUserCode();
        String userName = event.getUserName();
        String ip = event.getIp();
        OperationLog operationLog = new OperationLog();
        operationLog.setCreateBy(userCode);
        operationLog.setCreateByName(userName);
        operationLog.setCreateTime(event.getNowDate());
        operationLog.setTraceId(tid);
        operationLog.setInvoiceNo(operationLogEnum.getInvoiceNo(source));
        operationLog.setInvoiceType(operationLogEnum.getInvoiceType(source));
        operationLog.setType(operationLogEnum.getType());
        operationLog.setContent(operationLogEnum.getLog(source));
        operationLog.setIp(ip);
        this.operationLogService.insertOperationLog(operationLog);
    }
}
