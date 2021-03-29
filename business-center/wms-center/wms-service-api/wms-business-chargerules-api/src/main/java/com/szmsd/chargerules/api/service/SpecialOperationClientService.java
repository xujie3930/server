package com.szmsd.chargerules.api.service;

import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
import com.szmsd.common.core.domain.R;

public interface SpecialOperationClientService {

    R add(BasSpecialOperationRequestDTO basSpecialOperationDTO);

}
