package com.szmsd.inventory.service;

import com.szmsd.inventory.domain.PurchaseDetails;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * 采购单 服务类
    * </p>
*
* @author 11
* @since 2021-04-26
*/
public interface IPurchaseDetailsService extends IService<PurchaseDetails> {

        /**
        * 查询采购单模块
        *
        * @param id 采购单模块ID
        * @return 采购单模块
        */
        PurchaseDetails selectPurchaseDetailsById(String id);

        /**
        * 查询采购单模块列表
        *
        * @param purchaseDetails 采购单模块
        * @return 采购单模块集合
        */
        List<PurchaseDetails> selectPurchaseDetailsList(PurchaseDetails purchaseDetails);

        /**
        * 新增采购单模块
        *
        * @param purchaseDetails 采购单模块
        * @return 结果
        */
        int insertPurchaseDetails(PurchaseDetails purchaseDetails);

        /**
        * 修改采购单模块
        *
        * @param purchaseDetails 采购单模块
        * @return 结果
        */
        int updatePurchaseDetails(PurchaseDetails purchaseDetails);

        /**
        * 批量删除采购单模块
        *
        * @param ids 需要删除的采购单模块ID
        * @return 结果
        */
        int deletePurchaseDetailsByIds(List<String> ids);

        /**
        * 删除采购单模块信息
        *
        * @param id 采购单模块ID
        * @return 结果
        */
        int deletePurchaseDetailsById(String id);

}

