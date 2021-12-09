package com.szmsd.exception.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.exception.domain.ExceptionInfo;
import com.szmsd.exception.dto.ExceptionInfoExportDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author l
 * @since 2021-03-30
 */
public interface ExceptionInfoMapper extends BaseMapper<ExceptionInfo> {

    List<ExceptionInfoExportDto> exportList(@Param(Constants.WRAPPER) QueryWrapper<ExceptionInfo> queryWrapper);
}
