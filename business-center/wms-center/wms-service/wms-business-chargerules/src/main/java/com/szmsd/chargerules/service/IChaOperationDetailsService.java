package com.szmsd.chargerules.service;

import com.szmsd.chargerules.domain.ChaOperationDetails;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.vo.ChaOperationDetailsVO;

import java.util.List;

/**
 * <p>
 * 费用规则明细表 服务类
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
public interface IChaOperationDetailsService extends IService<ChaOperationDetails> {

    /**
     * 新增、修改规则明细
     *
     * @param dto
     */
    void saveOrUpdateDetailList(OperationDTO dto);

    Integer deleteByOperationId(Integer id);

    List<ChaOperationDetailsVO> queryDetailByOpeId(Long id);
    List<ChaOperationDetailsVO> queryDetailByOpeIdList(List<Long> idList);
}

