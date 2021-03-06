package com.szmsd.delivery.api.service;

import com.szmsd.delivery.dto.DelOutboundDto;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:35
 */
public interface DelOutboundClientService {

    /**
     * 出库管理 - 创建
     *
     * @param dto dto
     * @return int
     */
    int insertDelOutbound(DelOutboundDto dto);
}
