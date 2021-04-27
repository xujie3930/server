package com.szmsd.inventory.service;

import com.szmsd.inventory.domain.PurchaseStorageDetails;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * 采购单 入库详情 服务类
    * </p>
*
* @author 11
* @since 2021-04-26
*/
public interface IPurchaseStorageDetailsService extends IService<PurchaseStorageDetails> {

        /**
        * 查询采购单 入库详情模块
        *
        * @param id 采购单 入库详情模块ID
        * @return 采购单 入库详情模块
        */
        PurchaseStorageDetails selectPurchaseStorageDetailsById(String id);

        /**
        * 查询采购单 入库详情模块列表
        *
        * @param purchaseStorageDetails 采购单 入库详情模块
        * @return 采购单 入库详情模块集合
        */
        List<PurchaseStorageDetails> selectPurchaseStorageDetailsList(PurchaseStorageDetails purchaseStorageDetails);

        /**
        * 新增采购单 入库详情模块
        *
        * @param purchaseStorageDetails 采购单 入库详情模块
        * @return 结果
        */
        int insertPurchaseStorageDetails(PurchaseStorageDetails purchaseStorageDetails);

        /**
        * 修改采购单 入库详情模块
        *
        * @param purchaseStorageDetails 采购单 入库详情模块
        * @return 结果
        */
        int updatePurchaseStorageDetails(PurchaseStorageDetails purchaseStorageDetails);

        /**
        * 批量删除采购单 入库详情模块
        *
        * @param ids 需要删除的采购单 入库详情模块ID
        * @return 结果
        */
        int deletePurchaseStorageDetailsByIds(List<String> ids);

        /**
        * 删除采购单 入库详情模块信息
        *
        * @param id 采购单 入库详情模块ID
        * @return 结果
        */
        int deletePurchaseStorageDetailsById(String id);

}

