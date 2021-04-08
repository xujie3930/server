package com.szmsd.chargerules.service;


import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.vo.OperationVo;

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
    OperationVo details(int id);
}
