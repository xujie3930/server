package com.szmsd.track.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.track.domain.TrackTyRequestLog;

public interface IDelTyRequestLogService extends IService<TrackTyRequestLog> {

    void handler(TrackTyRequestLog tyRequestLog);
}
