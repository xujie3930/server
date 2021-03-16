package com.szmsd.http.service;

import com.szmsd.http.dto.*;
import com.szmsd.http.vo.ResponseVO;

public interface IBasService {

    /**
     * 新增/修改物料
     * @param packingRequest
     * @return
     */
    ResponseVO createPacking(PackingRequest packingRequest);

    /**
     * 新增修改sku/包材
     * @param productRequest
     * @return
     */
    ResponseVO createProduct(ProductRequest productRequest);

    /**
     * 新增修改卖家
     * @param sellerRequest
     * @return
     */
    ResponseVO createSeller(SellerRequest sellerRequest);

    /**
     * 新增特殊操作
     * @param specialOperationRequest specialOperationRequest
     * @return ResponseVO
     */
    ResponseVO save(SpecialOperationRequest specialOperationRequest);

    /**
     * 更新特殊操作结果
     * @param specialOperationResultRequest specialOperationResultRequest
     * @return ResponseVO
     */
    ResponseVO update(SpecialOperationResultRequest specialOperationResultRequest);
}
