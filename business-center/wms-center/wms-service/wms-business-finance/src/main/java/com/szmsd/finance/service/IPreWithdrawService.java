package com.szmsd.finance.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.PreWithdraw;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreWithdrawDTO;

import java.util.List;

/**
 * @author liulei
 */
public interface IPreWithdrawService {
    List<PreWithdraw> listPage(PreWithdrawDTO dto);

    R save(PreWithdrawDTO dto);

    R audit(PreRechargeAuditDTO dto);
}
