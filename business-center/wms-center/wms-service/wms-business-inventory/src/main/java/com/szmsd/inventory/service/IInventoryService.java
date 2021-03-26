package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;

import java.util.List;

public interface IInventoryService extends IService<Inventory> {

    void inbound(InboundInventoryDTO inboundInventoryDTO);

    List<InventorySkuVO> selectList(InventorySkuQueryDTO inventorySkuQueryDTO);

    /**
     * 根据仓库编码，SKU查询可用库存
     *
     * @param queryDto queryDto
     * @return InventoryAvailableDto
     */
    List<InventoryAvailableListVO> queryAvailableList(InventoryAvailableQueryDto queryDto);

    /**
     * 批量冻结库存
     *
     * @param freezeListDto freezeListDto
     * @return int
     */
    int freeze(InventoryFreezeListDto freezeListDto);

    /**
     * 批量释放冻结库存
     *
     * @param freezeListDto freezeListDto
     * @return int
     */
    int unFreeze(InventoryFreezeListDto freezeListDto);

    /**
     * 批量扣减库存
     *
     * @param deductionListDto deductionListDto
     * @return int
     */
    int deduction(InventoryDeductionListDto deductionListDto);

    /**
     * 批量释放扣减库存
     *
     * @param deductionListDto deductionListDto
     * @return int
     */
    int unDeduction(InventoryDeductionListDto deductionListDto);
}

