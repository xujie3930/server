package com.szmsd.finance.handler;

import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.finance.mapper.AccountBalanceLogMapper;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.vo.BillGeneratorRequestVO;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
public class BillGeneratorBO {

    private AccountSerialBillMapper accountSerialBillMapper;
    private AccountBalanceLogMapper accountBalanceLogMapper;
    private BasFeignService basFeignService;
    private BillGeneratorRequestVO billRequestVO;
    private BasSellerInfoVO basSellerInfoVO;
    private String filePath;
    private String recordId;

    private HttpServletRequest request;

}
