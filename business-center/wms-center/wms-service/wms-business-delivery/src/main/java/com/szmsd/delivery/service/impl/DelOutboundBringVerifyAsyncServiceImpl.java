package com.szmsd.delivery.service.impl;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.service.IDelOutboundBringVerifyAsyncService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.ApplicationContainer;
import com.szmsd.delivery.service.wrapper.ApplicationContext;
import com.szmsd.delivery.service.wrapper.BringVerifyEnum;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DelOutboundBringVerifyAsyncServiceImpl implements IDelOutboundBringVerifyAsyncService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundBringVerifyAsyncServiceImpl.class);

    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IDelOutboundService delOutboundService;

    @Transactional
    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_DELOUTBOUND_REVIEWED)
    @Override
    public void bringVerifyAsync(DelOutbound delOutbound) {
        ApplicationContext context = this.delOutboundBringVerifyService.initContext(delOutbound);
        BringVerifyEnum currentState;
        String bringVerifyState = delOutbound.getBringVerifyState();
        if (StringUtils.isEmpty(bringVerifyState)) {
            currentState = BringVerifyEnum.BEGIN;
        } else {
            currentState = BringVerifyEnum.get(bringVerifyState);
            // 兼容
            if (null == currentState) {
                currentState = BringVerifyEnum.BEGIN;
            }
        }
        ApplicationContainer applicationContainer = new ApplicationContainer(context, currentState, BringVerifyEnum.END, BringVerifyEnum.BEGIN);
        try {
            applicationContainer.action();
        } catch (CommonException e) {
            // 回滚操作
            applicationContainer.setEndState(BringVerifyEnum.BEGIN);
            applicationContainer.rollback();
            // 更新状态
            DelOutbound updateDelOutbound = new DelOutbound();
            updateDelOutbound.setId(delOutbound.getId());
            updateDelOutbound.setBringVerifyState(BringVerifyEnum.BEGIN.name());
            this.delOutboundService.updateById(updateDelOutbound);
            // 抛出异常
            // throw e;
            // 异步屏蔽异常，将异常打印到日志中
            // 异步错误在单据里面会显示错误信息
            this.logger.error("提审异步操作失败，" + e.getMessage(), e);
        }
    }

}
