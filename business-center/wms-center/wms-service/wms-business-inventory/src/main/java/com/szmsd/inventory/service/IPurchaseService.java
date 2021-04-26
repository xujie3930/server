package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Purchase;
import com.szmsd.inventory.domain.dto.PurchaseAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseInfoAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseQueryDTO;
import com.szmsd.inventory.domain.vo.PurchaseInfoDetailVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoListVO;

import java.util.List;

/**
 * <p>
 * 采购单 服务类
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
public interface IPurchaseService extends IService<Purchase> {

    /**
     * 查询采购单模块
     *
     * @param id 采购单模块ID
     * @return 采购单模块
     */
    PurchaseInfoDetailVO selectPurchaseByPurchaseNo(String id);

    /**
     * 查询采购单模块列表
     *
     * @param purchaseQueryDTO 采购单模块
     * @return 采购单模块集合
     */
    List<PurchaseInfoListVO> selectPurchaseList(PurchaseQueryDTO purchaseQueryDTO);


    /**
     * 批量删除采购单模块
     *
     * @param ids 需要删除的采购单模块ID
     * @return 结果
     */
    int deletePurchaseByIds(List<String> ids);

    /**
     * 删除采购单模块信息
     *
     * @param id 采购单模块ID
     * @return 结果
     */
    int deletePurchaseById(String id);

    int insertPurchaseBatch(PurchaseInfoAddDTO purchase);

    List<PurchaseInfoListVO> selectPurchaseListClient(PurchaseQueryDTO purchaseQueryDTO);
}

