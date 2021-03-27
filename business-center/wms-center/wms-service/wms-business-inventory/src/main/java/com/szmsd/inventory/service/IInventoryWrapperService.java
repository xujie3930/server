package com.szmsd.inventory.service;

import com.szmsd.inventory.domain.dto.InventoryOperateListDto;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 19:20
 */
public interface IInventoryWrapperService {

    /**
     * 批量冻结库存
     *
     * @param operateListDto operateListDto
     * @return int
     */
    int freeze(InventoryOperateListDto operateListDto);

    /**
     * 批量释放冻结库存
     *
     * @param operateListDto operateListDto
     * @return int
     */
    int unFreeze(InventoryOperateListDto operateListDto);

    /**
     * 重置冻结
     *
     * @param operateListDto operateListDto
     * @return int
     */
    int unFreezeAndFreeze(InventoryOperateListDto operateListDto);

    /**
     * 批量扣减库存
     *
     * @param operateListDto operateListDto
     * @return int
     */
    int deduction(InventoryOperateListDto operateListDto);

    /**
     * 批量释放扣减库存
     *
     * @param operateListDto operateListDto
     * @return int
     */
    int unDeduction(InventoryOperateListDto operateListDto);

    /**
     * 重置扣减
     *
     * @param operateListDto operateListDto
     * @return int
     */
    int unDeductionAndDeduction(InventoryOperateListDto operateListDto);
}
