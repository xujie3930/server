package com.szmsd.track.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.track.domain.DelTyRequestLog;

public interface IDelTyRequestLogService extends IService<DelTyRequestLog> {

    void handler(DelTyRequestLog tyRequestLog);
}
