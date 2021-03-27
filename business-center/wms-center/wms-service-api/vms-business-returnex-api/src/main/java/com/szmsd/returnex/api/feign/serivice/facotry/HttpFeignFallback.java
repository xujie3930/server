package com.szmsd.returnex.api.feign.serivice.facotry;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import com.szmsd.returnex.api.feign.serivice.IHttpFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: HttpFeignFallback
 * @Description: Http服务调用异常处理
 * @Author: 11
 * @Date: 2021/3/27 14:07
 */
@Slf4j
@Component
public class HttpFeignFallback implements FallbackFactory<IHttpFeignService> {

    @Override
    public IHttpFeignService create(Throwable throwable) {
        return new IHttpFeignService() {

            @Override
            public R<CreateExpectedRespVO> expectedCreate(CreateExpectedReqDTO expectedReqDTO) {
                log.error("#F1 创建退件预报 调用【expectedCreate】异常 info:{}", expectedReqDTO);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ProcessingUpdateRespVO> processingUpdate(ProcessingUpdateReqDTO processingUpdateReqDTO) {
                log.error("#F2 接收客户提供的处理方式 调用【processingUpdate】异常 info:{}", processingUpdateReqDTO);
                return R.convertResultJson(throwable);
            }
        };
    }
}
