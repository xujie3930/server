package com.szmsd.delivery.event.listener;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelCk1RequestLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.service.IDelCk1RequestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateStatusDelCk1RequestLogCallback implements DelCk1RequestLogCallback {

    @Autowired
    private IDelCk1RequestLogService delCk1RequestLogService;

    @Override
    public void callback(DelCk1RequestLog ck1RequestLog, String responseBody) {
        LambdaUpdateWrapper<DelCk1RequestLog> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(DelCk1RequestLog::getResponseBody, responseBody);
        updateWrapper.set(DelCk1RequestLog::getRemark, "callback");
        updateWrapper.eq(DelCk1RequestLog::getOrderNo, ck1RequestLog.getOrderNo());
        updateWrapper.eq(DelCk1RequestLog::getType, DelCk1RequestLogConstant.Type.create.name());
        this.delCk1RequestLogService.update(updateWrapper);
    }
}
