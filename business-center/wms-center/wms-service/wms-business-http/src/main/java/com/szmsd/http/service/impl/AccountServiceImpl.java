package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.service.IAccountService;
import com.szmsd.http.vo.RechargesResponseVo;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
        dto.setNotifyUrl(httpConfig.getNotifyUrl());
        return JSON.parseObject(httpPost("", dto), RechargesResponseVo.class);
    }

    @Override
    public RechargesResponseVo rechargeResult(String rechargeNo) {
        return JSON.parseObject(httpPost("/", rechargeNo), RechargesResponseVo.class);
    }

    @Override
    public String getUrl(){
        return httpConfig.getThirdPayment();
    }

    @Override
    Map<String, String> getHeaderMap() {
        Map<String,String> headerMap=new HashMap<String,String>();
        headerMap.put("Authorization","Bearer "+httpConfig.getRechargeToken());
        return headerMap;
    }
}
