package com.szmsd.chargerules.api.service.impl;

import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.chargerules.api.service.OperationClientService;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class OperationClientServiceImpl implements OperationClientService {

    @Resource
    private OperationFeignService operationFeignService;

    @Override
    public R delOutboundFreeze(DelOutboundVO delOutboundVO) {
        return operationFeignService.delOutboundFreeze(delOutboundVO);
    }

    @Override
    public R delOutboundThaw(DelOutboundVO delOutboundVO) {
        return operationFeignService.delOutboundThaw(delOutboundVO);
    }

    @Override
    public R delOutboundCharge(DelOutboundVO delOutboundVO) {
        return operationFeignService.delOutboundCharge(delOutboundVO);
    }
}
