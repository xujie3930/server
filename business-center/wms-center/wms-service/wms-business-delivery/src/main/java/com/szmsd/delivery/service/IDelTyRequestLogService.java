package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelTyRequestLog;

public interface IDelTyRequestLogService extends IService<DelTyRequestLog> {

    void handler(DelTyRequestLog tyRequestLog);
}
