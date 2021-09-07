package com.szmsd.putinstorage.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.InboundCountVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
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

            @Override
            public R<List<InboundReceiptVO>> list(InboundReceiptQueryDTO queryDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InboundReceiptInfoVO>> saveOrUpdateBatch(List<CreateInboundReceiptDTO> createInboundReceiptDTOList) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R cancel(String warehouseNo) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R tracking(ReceivingTrackingRequest receivingCompletedRequest) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
