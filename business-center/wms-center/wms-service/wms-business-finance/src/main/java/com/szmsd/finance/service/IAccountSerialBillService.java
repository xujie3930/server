package com.szmsd.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.vo.BillBalanceVO;
import com.szmsd.finance.vo.EleBillQueryVO;
import com.szmsd.finance.vo.ElectronicBillVO;
import com.szmsd.finance.vo.BillGeneratorRequestVO;

import javax.servlet.http.HttpServletResponse;
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

    /**
     * 更新业务账单表 nature、business_type、charge_category_change 信息
     */
    void executeSerialBillNature();
}
