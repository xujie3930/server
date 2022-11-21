package com.szmsd.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.FssBank;
import com.szmsd.finance.domain.FssBankQueryVO;
import com.szmsd.finance.vo.FssBankVO;

import java.util.List;

public interface FssBankService extends IService<FssBank> {


    R<List<FssBankVO>> findAll();

    R<List<FssBankVO>> findBank();

    R<List<FssBankVO>> findBankAccount(String bankCode,String currencyCode);

    R<List<FssBankVO>> findBankAccount(FssBankQueryVO fssBankQueryVO);
}
