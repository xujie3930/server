package com.szmsd.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.domain.dto.InventoryRecordQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryRecordVO;

import java.util.List;

public interface InventoryRecordMapper extends BaseMapper<InventoryRecord> {

    List<InventoryRecordVO> selectList(InventoryRecordQueryDTO inventoryRecordQueryDTO);
}
