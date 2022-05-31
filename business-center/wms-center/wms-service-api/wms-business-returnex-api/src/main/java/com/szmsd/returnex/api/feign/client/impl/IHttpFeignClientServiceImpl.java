package com.szmsd.returnex.api.feign.client.impl;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import com.szmsd.returnex.api.feign.client.IHttpFeignClientService;
import com.szmsd.returnex.api.feign.serivice.IHttpFeignService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

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
        R<CreateExpectedRespVO> createExpectedRespVO = httpFeignService.expectedCreate(expectedReqDTO);
        log.info("创建退件预报 req:{} resp:{}", expectedReqDTO, JSONObject.toJSONString(createExpectedRespVO));
        checkSuccess(createExpectedRespVO);
        return R.getDataAndException(createExpectedRespVO);

    }

    @Override
    public ProcessingUpdateRespVO processingUpdate(ProcessingUpdateReqDTO processingUpdateReqDTO) {
        R<ProcessingUpdateRespVO> processingUpdateRespVOR = httpFeignService.processingUpdate(processingUpdateReqDTO);
        log.info("接收客户提供的处理方式 req:{} resp:{}", processingUpdateReqDTO, JSONObject.toJSONString(processingUpdateRespVOR));
        checkSuccess(processingUpdateRespVOR);
        return R.getDataAndException(processingUpdateRespVOR);
    }

    private void checkSuccess(R<? extends ResponseVO> checkVo) {
        log.info("返回结果{}", JSONObject.toJSONString(checkVo));
        Optional<? extends ResponseVO> responseOpt = Optional.ofNullable(checkVo).map(R::getData);
        boolean present = responseOpt.map(ResponseVO::getErrors).filter(StringUtils::isNotBlank).isPresent();
        boolean present1 = responseOpt.map(ResponseVO::getSuccess).orElse(true);
        if (present) {
            throw new RuntimeException(checkVo.getData().getErrors());
        }
        if (!present1) {
            String message = responseOpt.map(ResponseVO::getMessage).orElse("调用WMS返回异常");
            throw new RuntimeException(message);
        }
    }
}
