package com.szmsd.finance.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.vo.BillBalanceVO;
import com.szmsd.finance.vo.BillGeneratorRequestVO;
import com.szmsd.finance.vo.EleBillQueryVO;
import com.szmsd.finance.vo.ElectronicBillVO;

import java.util.List;

public interface AccountBillRecordService {

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
