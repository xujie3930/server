package com.szmsd.inventory.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventorySkuQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InventoryMapper extends BaseMapper<Inventory> {

    List<InventorySkuVO> selectList(InventorySkuQueryDTO inventorySkuQueryDTO);

    /**
     * 根据仓库编码，SKU查询可用库存
     *
     * @param queryWrapper queryWrapper
     * @return InventoryAvailableDto
     */
    List<InventoryAvailableListVO> queryAvailableList(@Param(Constants.WRAPPER) Wrapper<InventoryAvailableQueryDto> queryWrapper);
}
