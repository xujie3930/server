package com.szmsd.http.service;

import com.szmsd.http.dto.PackingRequest;
import com.szmsd.http.dto.ProductRequest;
import com.szmsd.http.dto.SellerRequest;
import com.szmsd.http.dto.SpecialOperationRequest;
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
}
