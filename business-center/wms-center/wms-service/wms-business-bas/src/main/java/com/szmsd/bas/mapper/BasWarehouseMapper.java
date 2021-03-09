package com.szmsd.bas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BasWarehouseCus;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.vo.BasWarehouseInfoVO;
import com.szmsd.bas.vo.BasWarehouseVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasWarehouseMapper extends BaseMapper<BasWarehouse> {

    List<BasWarehouseVO> selectList(BasWarehouseQueryDTO queryDTO);

    BasWarehouseInfoVO selectInfo(@Param("id") Long id, @Param("warehouseCode") String warehouseCode);

    List<BasWarehouseCus> selectWarehouseCus(@Param("warehouseCode") String warehouseCode, @Param("cusCode") String cusCode);

    void insertWarehouseCus(BasWarehouseCus basWarehouseCusList);
}
