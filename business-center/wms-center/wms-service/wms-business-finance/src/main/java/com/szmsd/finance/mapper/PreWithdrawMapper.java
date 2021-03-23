package com.szmsd.finance.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.finance.domain.PreWithdraw;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liulei
 */
public interface PreWithdrawMapper extends BaseMapper<PreWithdraw> {
    List<PreWithdraw> listPage(@Param(Constants.WRAPPER) LambdaQueryWrapper<PreWithdraw> queryWrapper);
}
