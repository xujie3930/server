package com.szmsd.delivery.service.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.event.EventUtil;
import com.szmsd.delivery.event.ShipmentPackingEvent;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundOpenService;
import com.szmsd.delivery.util.Utils;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.ShipmentUpdateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 14:53
 */
@Service
public class DelOutboundOpenServiceImpl implements IDelOutboundOpenService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundOpenServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;

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
                String orderType = delOutbound.getOrderType();
                boolean overWeight = false;
                // 验证重量有没有超出返回
                if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(orderType)
                        // 需要确认重量信息
                        && "076002".equals(delOutbound.getPackageConfirm())) {
                    // 仓库重量
                    double weight = Utils.defaultValue(dto.getWeight());
                    // 录单填写的重量
                    double weight1 = Utils.defaultValue(delOutbound.getWeight());
                    long deviation = Utils.defaultValue(delOutbound.getPackageWeightDeviation());
                    // 判断有没有超过误差重量
                    double abs = Math.abs(weight - weight1);
                    if (abs > deviation) {
                        // 超重，不再继续处理
                        overWeight = true;
                        // 发送超重的发货指令
                        ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                        shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                        shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                        shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                        shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                        shipmentUpdateRequestDto.setIsEx(true);
                        shipmentUpdateRequestDto.setExType("OutboundIntercept");
                        String templateFormat = "仓库称重{0}g，客户下单重量{1}g，差异值{2}g，超过误差范围{3}g，已拦截，请尽快提供处理意见。";
                        String exRemark = MessageFormat.format(templateFormat, weight, weight1, abs, deviation);
                        shipmentUpdateRequestDto.setExRemark(exRemark);
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                    }
                }
                // 更新包裹信息
                result = this.delOutboundService.shipmentPacking(dto, orderType);
                // 处理结果大于0才认定为执行成功
                if (result > 0 && !overWeight) {
                    // 执行异步任务
                    EventUtil.publishEvent(new ShipmentPackingEvent(delOutbound.getId()));
                }
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
