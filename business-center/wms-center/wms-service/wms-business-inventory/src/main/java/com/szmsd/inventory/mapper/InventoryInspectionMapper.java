package com.szmsd.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.InventoryInspection;
import com.szmsd.inventory.domain.dto.InventoryInspectionQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryInspectionDetailsVo;
import com.szmsd.inventory.domain.vo.InventoryInspectionVo;

import java.util.List;

public interface InventoryInspectionMapper extends BaseMapper<InventoryInspection> {

    /**
     * 查询验货单列表
     *
     * @param dto dto
     * @return result
     */
    List<InventoryInspectionVo> selectListPage(InventoryInspectionQueryDTO dto);

    /**
     * 查询验货单详情
     *
     * @param inspectionNo 单号
     * @return result
     */
    InventoryInspectionVo selectDetails(String inspectionNo);

    /**
     * 查询验货单SKU详情
     *
     * @param inspectionNo 单号
     * @return result
     */
    List<InventoryInspectionDetailsVo> selectInventoryInspectionDetails(String inspectionNo);
}
