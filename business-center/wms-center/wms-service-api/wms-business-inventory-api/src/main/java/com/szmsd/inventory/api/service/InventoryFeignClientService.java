package com.szmsd.inventory.api.service;

import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import com.szmsd.inventory.domain.vo.InventoryVO;

import java.util.List;

public interface InventoryFeignClientService {

    List<InventorySkuVolumeVO> querySkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO);

    List<InventoryAvailableListVO> queryAvailableList(InventoryAvailableQueryDto queryDto);

    InventoryAvailableListVO queryOnlyAvailable(InventoryAvailableQueryDto queryDto);

    List<InventoryVO> querySku(InventoryAvailableQueryDto queryDto);

    InventoryVO queryOnlySku(InventoryAvailableQueryDto queryDto);

    Integer freeze(InventoryOperateListDto operateListDto);

    Integer unFreeze(InventoryOperateListDto operateListDto);

    Integer unFreezeAndFreeze(InventoryOperateListDto operateListDto);

    Integer deduction(InventoryOperateListDto operateListDto);

    Integer unDeduction(InventoryOperateListDto operateListDto);

    Integer unDeductionAndDeduction(InventoryOperateListDto operateListDto);
}
