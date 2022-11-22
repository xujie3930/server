package com.szmsd.finance.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.PreWithdraw;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreWithdrawDTO;
import com.szmsd.finance.vo.PreWithdrawRejectVO;

import java.util.List;

/**
 * @author liulei
 */
public interface IPreWithdrawService {

    /**
     * 分页查询
     * @param dto
     * @return
     */
    List<PreWithdraw> listPage(PreWithdrawDTO dto);

    /**
     * 保存提现
     * @param dto
     * @return
     */
    R save(PreWithdrawDTO dto);

    /**
     * 审核提现
     * @param dto
     * @return
     */
    R audit(PreRechargeAuditDTO dto);

    /**
     * 提现退回
     * @param rejectVO
     * @return
     */
    R reject(PreWithdrawRejectVO rejectVO);
}
