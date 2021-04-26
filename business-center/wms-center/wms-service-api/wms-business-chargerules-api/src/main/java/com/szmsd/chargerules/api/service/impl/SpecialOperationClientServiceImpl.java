package com.szmsd.chargerules.api.service.impl;

import com.szmsd.chargerules.api.feign.SpecialOperationFeignService;
import com.szmsd.chargerules.api.service.SpecialOperationClientService;
import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class SpecialOperationClientServiceImpl implements SpecialOperationClientService {

    @Resource
    private SpecialOperationFeignService specialOperationFeignService;

    @Override
    public R add(BasSpecialOperationRequestDTO basSpecialOperationDTO) {
        return specialOperationFeignService.specialOperation(basSpecialOperationDTO);
    }

    @Override
    public R delOutboundCharge(DelOutboundVO delOutboundVO) {
        return specialOperationFeignService.delOutboundCharge(delOutboundVO);
    }

}
