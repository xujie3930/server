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
     * 电子账单
     * @param queryVO
     * @return
     */
    List<ElectronicBillVO> electronicPage(EleBillQueryVO queryVO);

    /**
     * 生成账单
     * @param billRequestVO
     * @return
     */
    R<Integer> generatorBill(BillGeneratorRequestVO billRequestVO);

    /**
     * 资金结余
     * @param queryVO
     * @return
     */
    List<BillBalanceVO> balancePage(EleBillQueryVO queryVO);
}
