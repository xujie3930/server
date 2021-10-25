package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.dto.UserCreditDetailDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liulei
 */
public interface AccountBalanceMapper extends BaseMapper<AccountBalance> {

    @DataScope("cus_code")
    List<AccountBalance> listPage(@Param(Constants.WRAPPER) LambdaQueryWrapper queryWrapper);

    /**
     * 修改授信額信息
     *
     * @param updateList
     * @param cusCode
     * @return
     */
    int updateCreditBatch(@Param("updateList") List<UserCreditDetailDTO> updateList, @Param("cusCode") String cusCode, @Param("currencyCodeList") List<String> currencyCodeList);

    List<AccountBalance> queryThePreTermBill();

}
