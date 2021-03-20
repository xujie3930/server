package com.szmsd.http.service;

import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.vo.RechargesResponseVo;

/**
 * @author liulei
 */
public interface IAccountService {
    RechargesResponseVo onlineRecharge(RechargesRequestDTO dto);

    RechargesResponseVo rechargeResult(String rechargeNo);
}
