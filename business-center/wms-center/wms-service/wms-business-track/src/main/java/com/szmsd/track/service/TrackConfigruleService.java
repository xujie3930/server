package com.szmsd.track.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.track.domain.TrackConfigRule;
import com.szmsd.track.vo.TrackConfigRuleQueryVO;

import java.util.List;

/**
 * <p>
 * 轨迹配置 服务类
 * </p>
 *
 * @author wxf
 * @since 2023-02-14
 */
public interface TrackConfigruleService extends IService<TrackConfigRule> {

    /**
     * 分页查询
     * @param trackConfigRuleQueryVO
     * @return
     */
    List<TrackConfigRule> selectPage(TrackConfigRuleQueryVO trackConfigRuleQueryVO);
}
