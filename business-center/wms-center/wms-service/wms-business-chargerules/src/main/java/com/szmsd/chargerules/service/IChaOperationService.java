package com.szmsd.chargerules.service;

import com.szmsd.chargerules.domain.ChaOperation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.vo.ChaOperationListVO;
import com.szmsd.chargerules.vo.ChaOperationVO;
import com.szmsd.chargerules.vo.OperationVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
public interface IChaOperationService extends IService<ChaOperation> {

    /**
     * 新增业务操作计费规则
     *
     * @param dto dto
     * @return result
     */
    int save(OperationDTO dto);

    /**
     * 修改
     *
     * @param dto
     * @return
     */
    int update(OperationDTO dto);

    /**
     * 详情
     *
     * @param id id
     * @return 详情
     */
    ChaOperationVO queryDetails(Long id);

    /**
     * 查询列表
     *
     * @return
     */
    /**
     * 查询列表
     *
     * @param queryDTO 查询条件
     * @return 列表
     */
    List<ChaOperationListVO> queryOperationList(OperationQueryDTO queryDTO);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    Integer deleteById(Integer id);
    /**
     * 查询用户的匹配规则 查询不校验
     * 用户类型.客户id二选一
     *
     * @param queryDTO 查询条件
     * @return 唯一生效的结果
     */
    ChaOperationVO queryOperationDetailByRule(OperationQueryDTO queryDTO);
}

