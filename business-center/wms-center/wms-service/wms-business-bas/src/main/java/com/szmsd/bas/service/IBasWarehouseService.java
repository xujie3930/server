package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BasWarehouseCus;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.dto.BasWarehouseStatusChangeDTO;
import com.szmsd.bas.dto.WarehouseKvDTO;
import com.szmsd.bas.vo.BasWarehouseInfoVO;
import com.szmsd.bas.vo.BasWarehouseVO;

import java.util.List;

/**
 * <p>
 * bas_warehouse - 仓库 服务类
 * </p>
 *
 * @author liangchao
 * @since 2021-03-06
 */
public interface IBasWarehouseService extends IService<BasWarehouse> {

    List<BasWarehouseVO> selectList(BasWarehouseQueryDTO queryDTO);

    void saveOrUpdate(AddWarehouseRequest addWarehouseRequest);

    BasWarehouseInfoVO queryInfo(String warehouseCode);

    void saveWarehouseCus(List<BasWarehouseCus> basWarehouseCusList);

    void statusChange(BasWarehouseStatusChangeDTO basWarehouseStatusChangeDTO);

    List<WarehouseKvDTO> selectInboundWarehouse();

    List<WarehouseKvDTO> selectCusInboundWarehouse();

    boolean vailCusWarehouse(String warehouseCode);

    /**
     * 根据仓库编码查询仓库信息
     *
     * @param warehouseCode warehouseCode
     * @return BasWarehouse
     */
    BasWarehouse queryByWarehouseCode(String warehouseCode);

    /**
     * 根据仓库编码查询仓库信息
     *
     * @param warehouseCodes warehouseCodes
     * @return BasWarehouse
     */
    List<BasWarehouse> queryByWarehouseCodes(List<String> warehouseCodes);
}

