package com.szmsd.finance.mapper;

import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.FssDeductionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.finance.vo.CreditUseInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 扣费信息记录表 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-10-14
 */
public interface DeductionRecordMapper extends BaseMapper<FssDeductionRecord> {

    void updateDeductionRecordStatus(List<AccountBalance> updateBillList);

    List<CreditUseInfo> queryTimeCreditUse(@Param("cusCode") String cusCode,@Param("statusList") List<Integer> statusList,@Param("currencyCodeList") List<String> currencyCodeList);

    Long moveInvalidCreditBill();

    Long removeInvalidCreditBill();

}
