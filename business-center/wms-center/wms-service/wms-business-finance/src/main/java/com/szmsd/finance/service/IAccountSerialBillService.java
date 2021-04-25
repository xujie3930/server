package com.szmsd.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;

import java.util.List;

public interface IAccountSerialBillService extends IService<AccountSerialBill> {

    List<AccountSerialBill> listPage(AccountSerialBillDTO dto);

    int add(AccountSerialBillDTO dto);

    boolean saveBatch(List<AccountSerialBillDTO> dto);
}
