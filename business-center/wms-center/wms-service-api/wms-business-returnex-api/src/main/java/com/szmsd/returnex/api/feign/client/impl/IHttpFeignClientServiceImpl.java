package com.szmsd.returnex.api.feign.client.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import com.szmsd.returnex.api.feign.client.IHttpFeignClientService;
import com.szmsd.returnex.api.feign.serivice.IHttpFeignService;
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
public class IHttpFeignClientServiceImpl implements IHttpFeignClientService {

    @Resource
    private IHttpFeignService httpFeignService;

    @Override
    public CreateExpectedRespVO expectedCreate(CreateExpectedReqDTO expectedReqDTO) {
        log.info("创建退件预报 req:{}",expectedReqDTO);
        return R.getDataAndException(httpFeignService.expectedCreate(expectedReqDTO));

    }

    @Override
    public ProcessingUpdateRespVO processingUpdate(ProcessingUpdateReqDTO processingUpdateReqDTO) {
        log.info("接收客户提供的处理方式 req:{}",processingUpdateReqDTO);
        return R.getDataAndException(httpFeignService.processingUpdate(processingUpdateReqDTO));
    }
}
