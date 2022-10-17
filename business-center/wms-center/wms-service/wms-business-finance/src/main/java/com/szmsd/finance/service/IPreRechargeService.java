package com.szmsd.finance.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.PreRecharge;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreRechargeDTO;

import java.util.List;

/**
 * @author liulei
 */
public interface IPreRechargeService {
    List<PreRecharge> listPage(PreRechargeDTO dto);

    R save(PreRechargeDTO dto);

    R audit(PreRechargeAuditDTO dto);
}
