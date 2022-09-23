package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.finance.domain.AccountBillRecord;
import com.szmsd.finance.vo.EleBillQueryVO;
import com.szmsd.finance.vo.ElectronicBillVO;

import java.util.List;

public interface AccountBillRecordMapper extends BaseMapper<AccountBillRecord> {


    List<ElectronicBillVO> electronicPage(EleBillQueryVO queryVO);
}
