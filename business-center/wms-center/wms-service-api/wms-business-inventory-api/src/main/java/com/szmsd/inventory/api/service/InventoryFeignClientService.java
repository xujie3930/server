package com.szmsd.inventory.api.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import com.szmsd.inventory.domain.vo.InventoryVO;
import com.szmsd.inventory.domain.vo.SkuInventoryAgeVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    List<SkuInventoryAgeVo> queryInventoryAgeBySku(String warehouseCode, String sku);

}
