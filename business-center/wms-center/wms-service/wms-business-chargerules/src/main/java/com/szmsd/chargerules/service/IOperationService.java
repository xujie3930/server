package com.szmsd.chargerules.service;


import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundOperationVO;

import java.util.List;

public interface IOperationService {

    /**
     * 新增业务操作计费规则
     * @param dto dto
     * @return result
     */
    int save(OperationDTO dto);

    /**
     * 修改业务操作计费规则
     * @param dto dto
     * @return result
     */
    int update(Operation dto);

    /**
     * 获取业务操作计费规则列表
     * @param dto dto
     * @return List<Operation>
     */
    List<Operation> listPage(OperationDTO dto);

    /**
     * 根据id查询详情
     * @param id id
     * @return Operation
     */
    Operation details(int id);

    Operation queryDetails(OperationDTO dto);

    /**
     * 出库扣款
     * @param dto dto
     * @return result
     */
    R delOutboundDeductions(DelOutboundOperationVO dto);

    /**
     * 出库冻结余额
     * @param delOutboundVO delOutboundVO
     * @return result
     */
    R delOutboundFreeze(DelOutboundOperationVO delOutboundVO);

    Operation getOperationDetails(DelOutboundOrderEnum outboundOrderEnum, OrderTypeEnum orderTypeEnum, String warehouseCode, Double weight);

    /**
     * 出库解冻余额
     * @param delOutboundVO delOutboundVO
     * @return result
     */
    R delOutboundThaw(DelOutboundOperationVO delOutboundVO);

    int deleteById(Integer id);
}
