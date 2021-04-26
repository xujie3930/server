package com.szmsd.chargerules.api.service;

import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundVO;

public interface SpecialOperationClientService {

    R add(BasSpecialOperationRequestDTO basSpecialOperationDTO);

    R delOutboundCharge(DelOutboundVO delOutboundVO);

}
