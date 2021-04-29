package com.szmsd.putinstorage.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.CreateInboundReceiptDTO;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.dto.ReceivingCompletedRequest;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import com.szmsd.putinstorage.domain.vo.InboundCountVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InboundReceiptFeignFallback implements FallbackFactory<InboundReceiptFeignService> {
    @Override
    public InboundReceiptFeignService create(Throwable throwable) {
        return new InboundReceiptFeignService() {
            @Override
            public R receiving(ReceivingRequest receivingRequest) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R completed(ReceivingCompletedRequest receivingCompletedRequest) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<InboundReceiptInfoVO> info(String warehouseNo) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<InboundReceiptInfoVO> saveOrUpdate(CreateInboundReceiptDTO createInboundReceiptDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InboundCountVO>> statistics(InboundReceiptQueryDTO queryDTO) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
