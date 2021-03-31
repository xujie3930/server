package com.szmsd.returnex.api.feign.serivice.facotry;

import com.szmsd.common.core.domain.R;
import com.szmsd.returnex.api.feign.serivice.IReturnExpressFeignService;
import com.szmsd.returnex.dto.ReturnArrivalReqDTO;
import com.szmsd.returnex.dto.ReturnProcessingReqDTO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ReturnExpressFeignFallback
 * @Description: VMS调用异常处理返回
 * @Author: 11
 * @Date: 2021/3/27 14:07
 */
@Slf4j
@Component
public class ReturnExpressFeignFallback implements FallbackFactory<IReturnExpressFeignService> {

    @Override
    public IReturnExpressFeignService create(Throwable throwable) {
        return new IReturnExpressFeignService() {
            @Override
            public R<Integer> saveArrivalInfoFormVms(ReturnArrivalReqDTO returnArrivalReqDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> updateProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
