package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.domain.AccountSerialBillTotalVO;
import com.szmsd.finance.dto.AccountBalanceBillCurrencyVO;
import com.szmsd.finance.dto.AccountBalanceBillResultDTO;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.dto.AccountSerialBillNatureDTO;
import com.szmsd.finance.vo.AccountSerialBillExcelVO;
import com.szmsd.finance.vo.BillBusinessTotalVO;
import com.szmsd.finance.vo.BillDirectDeliveryTotalVO;
import com.szmsd.finance.vo.EleBillQueryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountSerialBillMapper extends BaseMapper<AccountSerialBill> {

//    @DataScope("cus_code")
    List<AccountSerialBill> selectPageList(AccountSerialBillDTO dto);

    //根据单号查询其他信息
    DelOutbound  selectDelOutbound(@Param("no") String no);

    List<AccountBalanceBillResultDTO> findAccountBillResultData(EleBillQueryVO eleBillQueryVO);


    List<BillBusinessTotalVO> selectBusinessTotal(EleBillQueryVO queryVO);

    List<BillDirectDeliveryTotalVO> selectDirectDelivery(EleBillQueryVO queryVO);

    List<AccountSerialBill> selectBillDetails(EleBillQueryVO billDetailQueryVO);

    List<BillBusinessTotalVO> selectAllOrderType(EleBillQueryVO queryVO);

    List<BillBusinessTotalVO> recharge(EleBillQueryVO queryVO);

    List<BillBusinessTotalVO> withdrawal(EleBillQueryVO queryVO);

    List<BillBusinessTotalVO> supplementary(EleBillQueryVO queryVO);

    List<BillBusinessTotalVO> businessAll(EleBillQueryVO queryVO);

    List<BillBusinessTotalVO> balanceConversion(EleBillQueryVO queryVO);

    List<AccountSerialBillNatureDTO> selectBillOutbount(@Param("pageNum")Integer pageNum,@Param("pageSize") Integer pageSize);

    Integer selectBillOutbountCount();

    List<AccountBalanceBillCurrencyVO> findBillCurrencyData(AccountSerialBillDTO dto);

    List<AccountSerialBillTotalVO> selectBillTotal(AccountSerialBillDTO dto);

    List<AccountSerialBillExcelVO> exportData(AccountSerialBillDTO dto);

    List<BillBusinessTotalVO> discount(EleBillQueryVO queryVO);

    List<BillBusinessTotalVO> compensate(EleBillQueryVO queryVO);

    List<BillBusinessTotalVO> refund(EleBillQueryVO queryVO);
}
