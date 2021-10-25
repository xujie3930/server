package com.szmsd.finance.mapper;

import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.FssDeductionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

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
}
