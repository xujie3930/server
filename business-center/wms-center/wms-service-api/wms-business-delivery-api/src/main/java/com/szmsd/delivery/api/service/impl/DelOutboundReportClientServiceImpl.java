package com.szmsd.delivery.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundReportFeignService;
import com.szmsd.delivery.api.service.DelOutboundReportClientService;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-29 11:30
 */
@Service
public class DelOutboundReportClientServiceImpl implements DelOutboundReportClientService {

    private final DelOutboundReportFeignService delOutboundReportFeignService;

    public DelOutboundReportClientServiceImpl(DelOutboundReportFeignService delOutboundReportFeignService) {
        this.delOutboundReportFeignService = delOutboundReportFeignService;
    }

    @Override
    public List<DelOutboundReportListVO> queryBringVerifyData(DelOutboundReportQueryDto queryDto) {
        return R.getDataAndException(this.delOutboundReportFeignService.queryBringVerifyData(queryDto));
    }

    @Override
    public List<DelOutboundReportListVO> queryOutboundData(DelOutboundReportQueryDto queryDto) {
        return R.getDataAndException(this.delOutboundReportFeignService.queryOutboundData(queryDto));
    }
}
