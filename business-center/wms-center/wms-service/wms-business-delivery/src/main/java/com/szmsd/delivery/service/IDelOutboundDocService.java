package com.szmsd.delivery.service;

import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.vo.DelOutboundAddResponse;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:29
 */
public interface IDelOutboundDocService {

    List<DelOutboundAddResponse> add(List<DelOutboundDto> list);
}
