package com.szmsd.chargerules.service;


import com.msd.chargerules.domain.WarehouseOperation;
import com.msd.chargerules.dto.WarehouseOperationDTO;

import java.math.BigDecimal;
import java.util.List;

public interface IWarehouseOperationService {

    int save(WarehouseOperationDTO dto);

    int update(WarehouseOperation dto);

    List<WarehouseOperation> listPage(WarehouseOperationDTO dto);

    /**
     * 仓储服务计费
     * @param days 商品存放天数
     * @param cbm 商品体积
     * @param warehouseCode 仓库
     * @param dto dto
     * @return 价格
     */
    BigDecimal charge(int days, BigDecimal cbm, String warehouseCode, List<WarehouseOperation> dto);

}
