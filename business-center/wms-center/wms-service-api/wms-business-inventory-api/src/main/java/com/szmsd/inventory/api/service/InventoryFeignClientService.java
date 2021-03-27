package com.szmsd.inventory.api.service;

import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;

import java.util.List;

public interface InventoryFeignClientService {

    List<InventorySkuVolumeVO> querySkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO);

    List<InventoryAvailableListVO> queryAvailableList(InventoryAvailableQueryDto queryDto);

    Integer freeze(InventoryOperateListDto operateListDto);

    Integer unFreeze(InventoryOperateListDto operateListDto);

    Integer unFreezeAndFreeze(InventoryOperateListDto operateListDto);

    Integer deduction(InventoryOperateListDto operateListDto);

    Integer unDeduction(InventoryOperateListDto operateListDto);

    Integer unDeductionAndDeduction(InventoryOperateListDto operateListDto);
}
