package com.szmsd.delivery.mapper;

import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelQueryService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.domain.DelQueryServiceFeedback;
import com.szmsd.delivery.dto.DelQueryServiceDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 查件服务 Mapper 接口
 * </p>
 *
 * @author Administrator
 * @since 2022-06-08
 */
public interface DelQueryServiceMapper extends BaseMapper<DelQueryService> {

    List<String> selectsellerCode(@Param("username") String username);

    List<String> selectsellerCodes();

    List<String>  selectsellerCodeus(@Param("username")String username);

    List<DelOutbound>  selectDelOutbound();

    List<DelQueryService> selectListJobs(@Param("orderNo") String orderNo);

    List<DelQueryServiceFeedback>  selectDelQueryServiceFeedbackLists(Integer id);

    DelQueryServiceDto selectDelOutbounds(@Param("orderNo") String orderNo);

}
