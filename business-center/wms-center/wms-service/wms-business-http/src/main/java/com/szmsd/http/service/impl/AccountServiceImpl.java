package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.service.IAccountService;
import com.szmsd.http.vo.RechargesResponseVo;
import org.springframework.stereotype.Service;

/**
 * @author liulei
 */
@Service
public class AccountServiceImpl extends AbstractBaseHttpRequest implements IAccountService {

    public AccountServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public RechargesResponseVo onlineRecharge(RechargesRequestDTO dto) {
        return JSON.parseObject(httpPost(httpConfig.getThirdPayment(), dto), RechargesResponseVo.class);
    }
}
