package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 16:29
 */
@Service
public class DelOutboundAsyncServiceImpl implements IDelOutboundAsyncService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundAsyncServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;

    @Transactional
    @Override
    public int shipmentPacking(Long id) {
        // 获取新的出库单信息
        DelOutbound delOutbound = this.delOutboundService.getById(id);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据不存在");
        }
        ApplicationContext context = this.delOutboundBringVerifyService.initContext(delOutbound);
        ShipmentEnum currentState;
        String shipmentState = delOutbound.getShipmentState();
        if (StringUtils.isEmpty(shipmentState)) {
            currentState = ShipmentEnum.BEGIN;
        } else {
            currentState = ShipmentEnum.get(shipmentState);
        }
        new ApplicationContainer(context, currentState, ShipmentEnum.END, ShipmentEnum.BEGIN).action();
        return 1;
    }
}
