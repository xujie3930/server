package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpRmiFeignService;
import com.szmsd.http.api.service.IHtpRmiClientService;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HtpRmiClientServiceImpl implements IHtpRmiClientService {

    @Autowired
    private HtpRmiFeignService htpRmiFeignService;

    @Override
    public HttpResponseVO rmi(HttpRequestDto dto) {
        return R.getDataAndException(htpRmiFeignService.rmi(dto));
    }
}
