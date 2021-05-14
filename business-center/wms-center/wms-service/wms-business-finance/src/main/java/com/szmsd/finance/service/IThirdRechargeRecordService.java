package com.szmsd.finance.service;

import com.szmsd.finance.domain.ThirdRechargeRecord;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.RechargesCallbackRequestDTO;
import com.szmsd.http.vo.RechargesResponseVo;

/**
 * @author liulei
 */
public interface IThirdRechargeRecordService {
    void saveRecord(CustPayDTO dto, RechargesResponseVo vo);

    ThirdRechargeRecord updateRecordIfSuccess(RechargesCallbackRequestDTO dto);

    String getRechargeResult(String serialNo);
}
