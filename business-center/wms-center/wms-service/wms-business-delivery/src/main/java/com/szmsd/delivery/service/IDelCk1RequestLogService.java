package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelCk1RequestLog;

public interface IDelCk1RequestLogService extends IService<DelCk1RequestLog> {

    void handler(DelCk1RequestLog ck1RequestLog);
}
