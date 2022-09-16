package com.szmsd.delivery.mapper;

import com.szmsd.delivery.domain.DelQueryServiceFeedback;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.dto.DelQueryServiceFeedbackExc;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 查件服务反馈 Mapper 接口
 * </p>
 *
 * @author Administrator
 * @since 2022-06-08
 */
@Mapper
public interface DelQueryServiceFeedbackMapper extends BaseMapper<DelQueryServiceFeedback> {

    List<DelQueryServiceFeedbackExc>  selectLists(Integer id);
}
