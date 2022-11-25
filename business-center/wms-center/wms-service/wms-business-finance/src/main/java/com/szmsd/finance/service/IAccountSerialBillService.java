package com.szmsd.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountBalanceBillCurrencyVO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.vo.AccountSerialBillExcelVO;

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

    /**
     * 流水账单按币种统计
     * @param dto
     * @return
     */
    List<AccountBalanceBillCurrencyVO> findBillCurrencyData(AccountSerialBillDTO dto);

    void exportBillTotal(HttpServletResponse response, AccountSerialBillDTO dto);

    List<AccountSerialBillExcelVO> exportData(AccountSerialBillDTO dto);

    /**
     * 异步导出账单
     * @param response
     * @param dto
     */
     void asyncExport(HttpServletResponse response, AccountSerialBillDTO dto);

    R<Integer> exportCount(AccountSerialBillDTO dto);
}
