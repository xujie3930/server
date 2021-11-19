package com.szmsd.putinstorage.api.factory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.*;
import com.szmsd.putinstorage.domain.vo.InboundCountVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import com.szmsd.putinstorage.domain.vo.SkuInventoryStockRangeVo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class InboundReceiptFeignFallback implements FallbackFactory<InboundReceiptFeignService> {
    @Override
    public InboundReceiptFeignService create(Throwable throwable) {
        log.info("InboundReceiptFeignFallback Error", throwable);
        return new InboundReceiptFeignService() {
            @Override
            public R receiving(ReceivingRequest receivingRequest) {
                log.info("InboundReceiptFeignFallback receiving{}", JSONObject.toJSONString(receivingRequest), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R completed(ReceivingCompletedRequest receivingCompletedRequest) {
                log.info("InboundReceiptFeignFallback completed{}", JSONObject.toJSONString(receivingCompletedRequest), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<InboundReceiptInfoVO> info(String warehouseNo) {
                log.info("InboundReceiptFeignFallback info{}", JSONObject.toJSONString(warehouseNo), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<InboundReceiptInfoVO> saveOrUpdate(CreateInboundReceiptDTO createInboundReceiptDTO) {
                log.info("InboundReceiptFeignFallback saveOrUpdate{}", JSONObject.toJSONString(createInboundReceiptDTO), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InboundCountVO>> statistics(InboundReceiptQueryDTO queryDTO) {
                log.info("InboundReceiptFeignFallback statistics{}", JSONObject.toJSONString(queryDTO), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InboundReceiptVO>> list(InboundReceiptQueryDTO queryDTO) {
                log.info("InboundReceiptFeignFallback list{}", JSONObject.toJSONString(queryDTO), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InboundReceiptInfoVO>> saveOrUpdateBatch(List<CreateInboundReceiptDTO> createInboundReceiptDTOList) {
                log.info("InboundReceiptFeignFallback saveOrUpdateBatch{}", JSONObject.toJSONString(createInboundReceiptDTOList), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R cancel(String warehouseNo) {
                log.info("InboundReceiptFeignFallback cancel{}", JSONObject.toJSONString(warehouseNo), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R tracking(ReceivingTrackingRequest receivingCompletedRequest) {
                log.info("InboundReceiptFeignFallback tracking{}", JSONObject.toJSONString(receivingCompletedRequest), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public TableDataInfo<InboundReceiptVO> postPage(InboundReceiptQueryDTO queryDTO) {
                log.info("InboundReceiptFeignFallback postPage{}", JSONObject.toJSONString(queryDTO), throwable);
                throw new CommonException("查询异常");
            }

            @Override
            public R<List<SkuInventoryStockRangeVo>> querySkuStockByRange(InventoryStockByRangeDTO inventoryStockByRangeDTO) {
                log.info("InboundReceiptFeignFallback querySkuStockByRange{}", JSONObject.toJSONString(inventoryStockByRangeDTO), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> updateTracking(UpdateTrackingNoRequest updateTrackingNoRequest) {
                log.info("InboundReceiptFeignFallback updateTracking{}", updateTrackingNoRequest, throwable);
                return R.convertResultJson(throwable);
            }
        };
    }
}
