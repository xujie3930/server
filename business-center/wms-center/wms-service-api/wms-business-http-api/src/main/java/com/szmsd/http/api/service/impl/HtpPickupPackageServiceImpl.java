package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpOutboundFeignService;
import com.szmsd.http.api.feign.HttpPickupPackageFeignService;
import com.szmsd.http.api.service.IHtpPickupPackageService;
import com.szmsd.http.dto.CreatePickupPackageCommand;
import com.szmsd.http.vo.PickupPackageService;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : wangshuai
 * @date : 2022-03-25 16:22
 * @description :
 **/
@Service
public class HtpPickupPackageServiceImpl implements IHtpPickupPackageService {

    @Autowired
    private HttpPickupPackageFeignService httpPickupPackageFeignService;

    @Override
    public List<PickupPackageService> services() {
        return this.httpPickupPackageFeignService.services().getData();
    }

    @Override
    public ResponseVO create(CreatePickupPackageCommand createPickupPackageCommand) {
        return R.getDataAndException(this.httpPickupPackageFeignService.create(createPickupPackageCommand));
    }
}
