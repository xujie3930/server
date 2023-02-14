package com.szmsd.track.service.impl;

import com.szmsd.track.domain.TrackConfig;
import com.szmsd.track.mapper.TrackConfigMapper;
import com.szmsd.track.service.TrackConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定义轨迹状态 服务实现类
 * </p>
 *
 * @author wxf
 * @since 2023-02-14
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<TrackConfigMapper, TrackConfig> implements TrackConfigService {

}
