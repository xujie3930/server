package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.config.inner.api.ThirdPaymentApiConfig;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.service.IAccountService;
import com.szmsd.http.service.http.ThirdPaymentRequest;
import com.szmsd.http.vo.RechargesResponseVo;
import org.springframework.stereotype.Service;

/**
 * @author liulei
 */
@Service
public class AccountServiceImpl extends ThirdPaymentRequest implements IAccountService {

    public AccountServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public RechargesResponseVo onlineRecharge(RechargesRequestDTO dto) {
        ThirdPaymentApiConfig.Callback thirdPayment = httpConfig.getDefaultApiConfig().getThirdPayment().getCallback();
        dto.setNotifyUrl(thirdPayment.getNotifyUrl());
        if (StringUtils.isEmpty(dto.getBankCode())) {
            dto.setBankCode("");
        }
        if (StringUtils.isEmpty(dto.getRemark())) {
            dto.setRemark("");
        }
        return JSON.parseObject(httpPost("", "recharges.recharges", dto), RechargesResponseVo.class);
    }

    @Override
    public RechargesResponseVo rechargeResult(String rechargeNo) {
        return JSON.parseObject(httpPost("", "/", rechargeNo), RechargesResponseVo.class);
    }

}
