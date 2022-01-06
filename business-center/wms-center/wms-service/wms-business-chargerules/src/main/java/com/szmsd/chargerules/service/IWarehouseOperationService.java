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

    /**
     * 查询用户仓租规则，用户没查询到则查询用户类型
     *  用户类型.客户id二选一
     * @param setCusCodeList
     * @return
     */
    List<WarehouseOperationVo> selectOperationByRule(WarehouseOperationDTO queryDto);
}
