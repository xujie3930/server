package com.szmsd.chargerules.service;


import com.szmsd.chargerules.dto.WarehouseOperationDTO;
import com.szmsd.chargerules.vo.WarehouseOperationVo;

import java.math.BigDecimal;
import java.util.List;

public interface IWarehouseOperationService {

    int save(WarehouseOperationDTO dto);

    int update(WarehouseOperationDTO dto);

    List<WarehouseOperationVo> listPage(WarehouseOperationDTO dto);

    /**
     * 仓储服务计费
     * @param days 商品存放天数
     * @param cbm 商品体积
     * @param warehouseCode 仓库
     * @param dto dto
     * @return 价格
     */
    BigDecimal charge(int days, BigDecimal cbm, String warehouseCode, WarehouseOperationVo dto);

    /**
     * 根据id查询详情
     * @param id id
     * @return WarehouseOperationVo
     */
    WarehouseOperationVo details(int id);
}
