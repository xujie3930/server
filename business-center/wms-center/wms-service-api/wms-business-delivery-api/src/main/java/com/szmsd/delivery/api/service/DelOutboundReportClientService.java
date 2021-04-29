package com.szmsd.delivery.api.service;

import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.vo.DelOutboundReportListVO;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:32
 */
public interface DelOutboundReportClientService {

    /**
     * 查询提审数据
     *
     * @param queryDto queryDto
     * @return List<DelOutboundReportListVO>
     */
    List<DelOutboundReportListVO> queryBringVerifyData(DelOutboundReportQueryDto queryDto);

    /**
     * 查询出库数据
     *
     * @param queryDto queryDto
     * @return List<DelOutboundReportListVO>
     */
    List<DelOutboundReportListVO> queryOutboundData(DelOutboundReportQueryDto queryDto);
}
