package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Purchase;
import com.szmsd.inventory.domain.dto.PurchaseAddDTO;
import com.szmsd.inventory.domain.dto.PurchaseQueryDTO;
import com.szmsd.inventory.domain.vo.PurchaseInfoDetailVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoListVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoVO;

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
     * @param purchaseNo 采购单模块ID
     * @return 采购单模块
     */
    PurchaseInfoVO selectPurchaseByPurchaseNo(String purchaseNo);

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


    int insertPurchaseBatch(PurchaseAddDTO purchase);

    List<PurchaseInfoListVO> selectPurchaseListClient(PurchaseQueryDTO purchaseQueryDTO);

    int cancelByWarehouseNo(String warehouseNo);
}

