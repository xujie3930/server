package com.szmsd.delivery.service.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.dto.DelOutboundAgainTrackingNoDto;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundExceptionService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.vo.PricedProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class DelOutboundExceptionServiceImpl implements IDelOutboundExceptionService {

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;

    @Transactional
    @Override
    public boolean againTrackingNo(DelOutbound delOutbound, DelOutboundAgainTrackingNoDto dto) {
        String orderNo = delOutbound.getOrderNo();
        LambdaQueryWrapper<DelOutboundAddress> addressLambdaQueryWrapper = Wrappers.lambdaQuery();
        addressLambdaQueryWrapper.eq(DelOutboundAddress::getOrderNo, orderNo);
        this.delOutboundAddressService.remove(addressLambdaQueryWrapper);
        DelOutboundAddress delOutboundAddress = BeanMapperUtil.map(dto.getAddress(), DelOutboundAddress.class);
        if (Objects.nonNull(delOutboundAddress)) {
            delOutboundAddress.setOrderNo(orderNo);
            this.delOutboundAddressService.save(delOutboundAddress);
        }
        // 根据产品编码查询产品信息
        String productCode = dto.getShipmentRule();
        PricedProductInfo pricedProductInfo = htpPricedProductClientService.info(productCode);
        if (null == pricedProductInfo) {
            // 异常信息
            throw new CommonException("400", "查询产品[" + productCode + "]信息失败");
        }
        LambdaUpdateWrapper<DelOutbound> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(DelOutbound::getShipmentRule, productCode);
        lambdaUpdateWrapper.set(DelOutbound::getShipmentService, pricedProductInfo.getLogisticsRouteId());
        lambdaUpdateWrapper.set(DelOutbound::getTrackingAcquireType, DelOutboundTrackingAcquireTypeEnum.WAREHOUSE_SUPPLIER.getCode());
        lambdaUpdateWrapper.eq(DelOutbound::getId, delOutbound.getId());
        boolean update = delOutboundService.update(null, lambdaUpdateWrapper);
        Object[] params = new Object[]{delOutbound, productCode};
        DelOutboundOperationLogEnum.AGAIN_TRACKING_NO.listener(params);
        return update;
    }
}
