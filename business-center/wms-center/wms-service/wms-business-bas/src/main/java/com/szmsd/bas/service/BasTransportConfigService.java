package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasTransportConfig;
import com.szmsd.bas.dto.BasTransportConfigDTO;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface BasTransportConfigService {
    List<BasTransportConfig>  selectList(BasTransportConfigDTO queryDTO);

    R  updateBasTransportConfig(BasTransportConfig basTransportConfig);
}
