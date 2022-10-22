package com.szmsd.delivery.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.domain.BasShipmentRules;
import com.szmsd.delivery.dto.BasShipmentRulesDto;

import java.util.List;

public interface BasShipmenRulesService {
    List<BasShipmentRules> selectBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto);

    R  addBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto);

    R  updeteBasShipmentRules(BasShipmentRulesDto basShipmentRulesDto);

    R  deleteShipmentRules(BasShipmentRulesDto basShipmentRulesDto);
}
