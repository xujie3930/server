package com.szmsd.delivery.service.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.event.EventUtil;
import com.szmsd.delivery.event.ShipmentPackingEvent;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundOpenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 14:53
 */
@Service
public class DelOutboundOpenServiceImpl implements IDelOutboundOpenService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundOpenServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;

    @Override
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
        try {
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            String orderNo = dto.getOrderNo();
            queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
            DelOutbound delOutbound = this.delOutboundService.getOne(queryWrapper);
            if (null == delOutbound) {
                throw new CommonException("999", "单据不存在");
            }
            // 更新包裹信息
            int result;
            if (dto.isPackingMaterial()) {
                result = this.delOutboundService.shipmentPackingMaterial(dto);
            } else {
                result = this.delOutboundService.shipmentPacking(dto, delOutbound.getOrderType());
                // 执行异步任务
                EventUtil.publishEvent(new ShipmentPackingEvent(delOutbound.getId()));
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public int shipmentContainers(ShipmentContainersRequestDto dto) {
        try {
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            String orderNo = dto.getOrderNo();
            queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
            DelOutbound delOutbound = this.delOutboundService.getOne(queryWrapper);
            if (null == delOutbound) {
                throw new CommonException("999", "单据不存在");
            }
            // 更新包裹信息
            this.delOutboundService.shipmentContainers(dto);
            // 执行异步任务
            EventUtil.publishEvent(new ShipmentPackingEvent(delOutbound.getId()));
            return 1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
