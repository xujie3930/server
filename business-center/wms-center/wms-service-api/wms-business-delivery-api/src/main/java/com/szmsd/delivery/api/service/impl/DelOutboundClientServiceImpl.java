package com.szmsd.delivery.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.DelOutboundDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:36
 */
@Service
public class DelOutboundClientServiceImpl implements DelOutboundClientService {

    @Autowired
    private DelOutboundFeignService delOutboundFeignService;

    @Override
    public int insertDelOutbound(DelOutboundDto dto) {
        return R.getDataAndException(this.delOutboundFeignService.add(dto));
    }
}
