package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.api.service.IHtpIBasClientService;
import com.szmsd.http.dto.AddShipmentRuleRequest;
import com.szmsd.http.vo.BaseOperationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 15:35
 */
@Service
public class HtpIBasClientServiceImpl implements IHtpIBasClientService {

    @Autowired
    private HtpBasFeignService htpBasFeignService;

    @Override
    public BaseOperationResponse shipmentRule(AddShipmentRuleRequest addShipmentRuleRequest) {
        return R.getDataAndException(this.htpBasFeignService.shipmentRule(addShipmentRuleRequest));
    }
}
