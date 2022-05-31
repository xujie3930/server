package com.szmsd.returnex.api.feign.serivice.facotry;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.http.vo.returnex.ProcessingUpdateRespVO;
import com.szmsd.returnex.api.feign.serivice.IHttpFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: HttpFeignServiceFallback
 * @Description:
 * @Author: 11
 * @Date: 2021/3/29 16:13
 */
@Slf4j
@Component
public class HttpFeignServiceFallback implements FallbackFactory<IHttpFeignService> {
    @Override
    public IHttpFeignService create(Throwable throwable) {
        return new IHttpFeignService() {
            @Override
            public R<CreateExpectedRespVO> expectedCreate(CreateExpectedReqDTO expectedReqDTO) {
                log.error("创建退件预报 req:{} 【{}】", expectedReqDTO, throwable);
                throw new RuntimeException(String.format("创建退件预报异常%s", throwable.getMessage()));
            }

            @Override
            public R<ProcessingUpdateRespVO> processingUpdate(ProcessingUpdateReqDTO processingUpdateReqDTO) {
                log.error("接收客户提供的处理方式 req:{} 【{}】", processingUpdateReqDTO, throwable);
                throw new RuntimeException(String.format("接收客户提供的处理方式%s", throwable.getMessage()));
            }
        };
    }
}
