package com.szmsd.track.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.track.domain.TrackConfigRule;
import com.szmsd.track.mapper.TrackConfigRuleMapper;
import com.szmsd.track.service.TrackConfigruleService;
import com.szmsd.track.vo.TrackConfigRuleQueryVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 轨迹配置 服务实现类
 * </p>
 *
 * @author wxf
 * @since 2023-02-14
 */
@Service
public class ConfigRuleServiceImpl extends ServiceImpl<TrackConfigRuleMapper, TrackConfigRule> implements TrackConfigruleService {

    @Override
    public List<TrackConfigRule> selectPage(TrackConfigRuleQueryVO trackConfigRuleQueryVO) {



        return null;
    }
}
