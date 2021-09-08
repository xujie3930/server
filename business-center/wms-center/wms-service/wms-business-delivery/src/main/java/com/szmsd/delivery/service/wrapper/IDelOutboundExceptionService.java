package com.szmsd.delivery.service.wrapper;

import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundAgainTrackingNoDto;

public interface IDelOutboundExceptionService {

    boolean againTrackingNo(DelOutbound delOutbound, DelOutboundAgainTrackingNoDto dto);
}
