package com.szmsd.inventory.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventorySkuQueryDTO;
import com.szmsd.inventory.domain.dto.QueryFinishListDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;
import com.szmsd.inventory.domain.vo.InventoryVO;
import com.szmsd.inventory.domain.vo.QueryFinishListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InventoryMapper extends BaseMapper<Inventory> {
    @DataScope("t.cus_code")
    List<InventorySkuVO> selectListVO(InventorySkuQueryDTO inventorySkuQueryDTO);

    /**
     * 根据仓库编码，SKU查询可用库存
     *
     * @param queryWrapper queryWrapper
     * @return List<InventoryAvailableListVO>
     */
    List<InventoryAvailableListVO> queryAvailableList(@Param(Constants.WRAPPER) Wrapper<InventoryAvailableQueryDto> queryWrapper);

    /**
     * 根据仓库编码，SKU查询可用库存
     *
     * @param queryWrapper queryWrapper
     * @return InventoryAvailableListVO
     */
    InventoryAvailableListVO queryOnlyAvailable(@Param(Constants.WRAPPER) Wrapper<InventoryAvailableQueryDto> queryWrapper);

    /**
     * 查询SKU信息
     *
     * @param queryWrapper queryWrapper
     * @return List<InventoryVO>
     */
    List<InventoryVO> querySku(@Param(Constants.WRAPPER) Wrapper<InventoryAvailableQueryDto> queryWrapper);

    /**
     * 查询SKU信息
     *
     * @param queryWrapper queryWrapper
     * @return InventoryVO
     */
    InventoryVO queryOnlySku(@Param(Constants.WRAPPER) Wrapper<InventoryAvailableQueryDto> queryWrapper);

    List<QueryFinishListVO> queryFinishList(QueryFinishListDTO queryFinishListDTO);
}
