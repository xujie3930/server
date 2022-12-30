package com.szmsd.exception.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.exception.domain.ExceptionInfo;
import com.szmsd.exception.dto.ExceptionInfoDetailExportDto;
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

    List<ExceptionInfoDetailExportDto>  selectExceptionInfoDetailExport(@Param("orderNo") String orderNo);

    void updateDelOutboundDetail(ExceptionInfoDetailExportDto x);

    void updateDelOutboundEx(ExceptionInfoExportDto dto);

    List<String>  selectsellerCode(@Param("username") String username);

    List<String> selectsellerCodes();

    int selectExceptionInfoQuery(@Param(Constants.WRAPPER) QueryWrapper<ExceptionInfo> queryWrapper);

    int updExceptionInfoState(@Param("state") String state, @Param("orderNos") List<String> orderNos);

    void  updateDelOutboundHouseNo(ExceptionInfoExportDto dto);
}
