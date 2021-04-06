package com.szmsd.returnex.api.feign.client.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.returnex.api.feign.client.IBasFeignClientService;
import com.szmsd.returnex.api.feign.serivice.IBasFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName: IReturnExpressFeignClientService
 * @Description: 通过HTTP服务发起 http请求调用外部VMS接口
 * @Author: 11
 * @Date: 2021/3/27 14:21
 */
@Slf4j
@Service
public class IBasFeignClientServiceImpl implements IBasFeignClientService {

    @Resource
    private IBasFeignService basFeignService;

    @Override
    public String getLoginSellerCode() {
        return R.getDataAndException(basFeignService.getLoginSellerCode());
    }

}
