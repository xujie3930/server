package com.szmsd.http.config;

import com.szmsd.http.dto.*;
import com.szmsd.http.enums.RemoteConstant;
import com.szmsd.http.service.IBasService;
import com.szmsd.http.service.ICommonRemoteService;
import com.szmsd.http.service.IInboundService;
import com.szmsd.http.service.IRemoteExecutorTask;
import com.szmsd.http.vo.BaseOperationResponse;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * http请求代理类
 *
 * @ClassName: HttpSyncProxy
 * @Author: Administrator
 * @Date: 2022/3/18 21:45
 */
@Data
@Slf4j
@Component
public class HttpSyncProxy implements IBasService, IInboundService {

    @Resource
    private IBasService iBasService;
    @Resource
    private ICommonRemoteService iCommonRemoteService;
    /**
     * 新增/修改物料
     *
     * @param packingRequest
     * @return
     */
    @Override
    public ResponseVO createPacking(PackingRequest packingRequest) {
        return null;
    }

    /**
     * 新增修改sku
     *
     * @param productRequest
     * @return
     */
    @Override
    public ResponseVO createProduct(ProductRequest productRequest) {
        iCommonRemoteService.insertObj(productRequest, RemoteConstant.RemoteTypeEnum.WMS_SKU_CREATE);
        return creatSuccessResponse();
    }

    /**
     * 新增修改包材
     *
     * @param materialRequest
     * @return
     */
    @Override
    public ResponseVO createMaterial(MaterialRequest materialRequest) {
        return null;
    }

    /**
     * 新增修改卖家
     *
     * @param sellerRequest
     * @return
     */
    @Override
    public ResponseVO createSeller(SellerRequest sellerRequest) {
        return null;
    }

    /**
     * 新增特殊操作
     *
     * @param specialOperationRequest specialOperationRequest
     * @return ResponseVO
     */
    @Override
    public ResponseVO save(SpecialOperationRequest specialOperationRequest) {
        return null;
    }

    /**
     * 更新特殊操作结果
     *
     * @param specialOperationResultRequest specialOperationResultRequest
     * @return ResponseVO
     */
    @Override
    public ResponseVO update(SpecialOperationResultRequest specialOperationResultRequest) {
        return null;
    }

    /**
     * #A4 新增/修改发货规则（物流服务）
     *
     * @param addShipmentRuleRequest addShipmentRuleRequest
     * @return BaseOperationResponse
     */
    @Override
    public BaseOperationResponse shipmentRule(AddShipmentRuleRequest addShipmentRuleRequest) {
        return null;
    }

    @Override
    public ResponseVO inspection(AddSkuInspectionRequest request) {
        return null;
    }

    @Override
    public CreateReceiptResponse create(CreateReceiptRequest createReceiptRequestDTO) {
        iCommonRemoteService.insertObj(createReceiptRequestDTO, RemoteConstant.RemoteTypeEnum.WMS_INBOUND_CREATE);
        return creatSuccessResponse();
    }

    public static CreateReceiptResponse creatSuccessResponse(){
        CreateReceiptResponse createReceiptResponse = new CreateReceiptResponse();
        createReceiptResponse.setSuccess(true);
        return createReceiptResponse;
    }

    @Override
    public ResponseVO cancel(CancelReceiptRequest cancelReceiptRequestDTO) {
        return null;
    }

    @Override
    public ResponseVO createPackage(CreatePackageReceiptRequest createPackageReceiptRequest) {
        return null;
    }

    @Override
    public ResponseVO createTracking(CreateTrackRequest createTrackRequest) {
        //iCommonRemoteService.insertObj(createTrackRequest, RemoteConstant.RemoteTypeEnum.WMS_INBOUND_LOGISTICS_CREATE);
        iCommonRemoteService.insertObj(createTrackRequest, RemoteConstant.RemoteTypeEnum.WMS_INBOUND_CREATE);
        return creatSuccessResponse();
    }
}
