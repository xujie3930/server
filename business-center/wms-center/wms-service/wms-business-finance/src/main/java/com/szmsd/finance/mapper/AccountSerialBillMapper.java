package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountSerialBillMapper extends BaseMapper<AccountSerialBill> {

//    @DataScope("cus_code")
    List<AccountSerialBill> selectPageList(AccountSerialBillDTO dto);

    //根据单号查询其他信息
    DelOutbound  selectDelOutbound(@Param("no") String no);
}
