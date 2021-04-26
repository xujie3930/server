package com.szmsd.chargerules.api.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundVO;

public interface OperationClientService {

    R delOutboundFreeze(DelOutboundVO delOutboundVO);

    R delOutboundThaw(DelOutboundVO delOutboundVO);

    R delOutboundCharge(DelOutboundVO delOutboundVO);

}
