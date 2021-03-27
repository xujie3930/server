package com.szmsd.inventory.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventoryOperateListDto;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-27 10:18
 */
@Service
public class InventoryFeignClientServiceImpl implements InventoryFeignClientService {

    @Autowired
    private InventoryFeignService inventoryFeignService;

    @Override
    public List<InventorySkuVolumeVO> querySkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO) {
        return R.getDataAndException(this.inventoryFeignService.querySkuVolume(inventorySkuVolumeQueryDTO));
    }

    @Override
    public List<InventoryAvailableListVO> queryAvailableList(InventoryAvailableQueryDto queryDto) {
        TableDataInfo<InventoryAvailableListVO> tableDataInfo = this.inventoryFeignService.queryAvailableList(queryDto);
        if (null != tableDataInfo) {
            return tableDataInfo.getRows();
        }
        return null;
    }

    @Override
    public Integer freeze(InventoryOperateListDto operateListDto) {
        return R.getDataAndException(this.inventoryFeignService.freeze(operateListDto));
    }

    @Override
    public Integer unFreeze(InventoryOperateListDto operateListDto) {
        return R.getDataAndException(this.inventoryFeignService.unFreeze(operateListDto));
    }

    @Override
    public Integer unFreezeAndFreeze(InventoryOperateListDto operateListDto) {
        return R.getDataAndException(this.inventoryFeignService.unFreezeAndFreeze(operateListDto));
    }

    @Override
    public Integer deduction(InventoryOperateListDto operateListDto) {
        return R.getDataAndException(this.inventoryFeignService.deduction(operateListDto));
    }

    @Override
    public Integer unDeduction(InventoryOperateListDto operateListDto) {
        return R.getDataAndException(this.inventoryFeignService.unDeduction(operateListDto));
    }

    @Override
    public Integer unDeductionAndDeduction(InventoryOperateListDto operateListDto) {
        return R.getDataAndException(this.inventoryFeignService.unDeductionAndDeduction(operateListDto));
    }
}
