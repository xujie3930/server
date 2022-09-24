package com.szmsd.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;

import java.util.List;

public interface IAccountSerialBillService extends IService<AccountSerialBill> {

    List<AccountSerialBill> listPage(AccountSerialBillDTO dto);

    int add(AccountSerialBillDTO dto);

    boolean saveBatch(List<AccountSerialBillDTO> dto);
    /**
     * 幂等校验 校验重复扣费 ： 单号—发生额-业务类型
     * @return
     */
    boolean checkForDuplicateCharges(CustPayDTO dto);
}
