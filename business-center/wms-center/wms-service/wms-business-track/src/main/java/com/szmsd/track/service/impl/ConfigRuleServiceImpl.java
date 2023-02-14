package com.szmsd.track.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.support.BaseAware;
import com.szmsd.common.core.web.controller.QueryDto;
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
public class ConfigRuleServiceImpl extends ServiceImpl<TrackConfigRuleMapper, TrackConfigRule> implements TrackConfigruleService, BaseAware {

    @Override
    public List<TrackConfigRule> selectPage(TrackConfigRuleQueryVO trackConfigRuleQueryVO) {

        LambdaQueryWrapper<TrackConfigRule> trackconfigQueryWrapper = Wrappers.lambdaQuery();

        trackconfigQueryWrapper.eq(TrackConfigRule::getOmsTrackStatus,trackConfigRuleQueryVO.getOmsTrackStatus());
        trackconfigQueryWrapper.eq(TrackConfigRule::getTyTrackStatusCn,trackConfigRuleQueryVO.getTyTrackStatusCn());
        trackconfigQueryWrapper.eq(TrackConfigRule::getPrcTerminalCarrier,trackConfigRuleQueryVO.getPrcTerminalCarrier());
        trackconfigQueryWrapper.eq(TrackConfigRule::getKeyword,trackConfigRuleQueryVO.getKeyword());
        trackconfigQueryWrapper.eq(TrackConfigRule::getCountryCode,trackConfigRuleQueryVO.getCountryCode());

        QueryDto queryDto = new QueryDto();
        queryDto.setPageNum(trackConfigRuleQueryVO.getPageNum());
        queryDto.setPageSize(trackConfigRuleQueryVO.getPageSize());
        startPage(queryDto);
        List<TrackConfigRule> trackConfigRules = baseMapper.selectList(trackconfigQueryWrapper);
        return trackConfigRules;
    }
}
