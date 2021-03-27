package com.szmsd.returnex.api.feign.client.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import com.szmsd.returnex.api.feign.client.IHttpFeignClientService;
import com.szmsd.returnex.api.feign.serivice.IHttpFeignService;

import javax.annotation.Resource;

/**
 * @ClassName: HttpFeignClientServiceImpl
 * @Description: Http调用实现
 * @Author: 11
 * @Date: 2021/3/27 14:22
 */
public class HttpFeignClientServiceImpl implements IHttpFeignClientService {

    @Resource
    private IHttpFeignService iHttpFeignService;

    @Override
    public R<CreateExpectedRespVO> expectedCreate(CreateExpectedReqDTO expectedReqDTO) {
        return iHttpFeignService.expectedCreate(expectedReqDTO);
    }

    @Override
    public R<ProcessingUpdateRespVO> processingUpdate(ProcessingUpdateReqDTO processingUpdateReqDTO) {
        return iHttpFeignService.processingUpdate(processingUpdateReqDTO);
    }
}
