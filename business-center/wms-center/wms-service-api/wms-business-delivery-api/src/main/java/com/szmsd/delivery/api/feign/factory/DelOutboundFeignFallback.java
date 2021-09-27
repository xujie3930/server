package com.szmsd.delivery.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.vo.*;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import com.szmsd.http.vo.PricedProduct;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:33
 */
@Component
public class DelOutboundFeignFallback implements FallbackFactory<DelOutboundFeignService> {
    @Override
    public DelOutboundFeignService create(Throwable throwable) {
        return new DelOutboundFeignService() {
            @Override
            public R<Integer> shipment(ShipmentRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> shipmentContainers(ShipmentContainersRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<DelOutbound> details(String orderId) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundDetailListVO>> getDelOutboundDetailsList(DelOutboundListQueryDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<TableDataInfo<QueryChargeVO>> getDelOutboundCharge(QueryChargeDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundDetailVO>> createPurchaseOrderListByIdList(List<String> idList) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundDetailVO>> getTransshipmentProductData(List<String> idList) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R setPurchaseNo(String purchaseNo, List<String> orderNoList) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> furtherHandler(DelOutboundFurtherHandlerDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public TableDataInfo<DelOutboundListVO> page(DelOutboundListQueryDto queryDto) {
                throw new BaseException("查询出库单异常:", throwable.getMessage());
            }

            @Override
            public R<Integer> canceled(DelOutboundCanceledDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<PricedProduct>> inService(DelOutboundOtherInServiceDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundAddResponse>> add(List<DelOutboundDto> dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundLabelResponse>> labelBase64(DelOutboundLabelDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> uploadBoxLabel(DelOutboundUploadBoxLabelDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundPacking>> queryList(DelOutboundPacking request) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> againTrackingNo(DelOutboundAgainTrackingNoDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundListExceptionMessageVO>> exceptionMessageList(List<String> orderNos) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundBringVerifyVO>> bringVerify(DelOutboundBringVerifyDto dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
