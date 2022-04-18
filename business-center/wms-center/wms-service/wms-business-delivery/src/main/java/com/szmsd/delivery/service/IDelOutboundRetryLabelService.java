package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundRetryLabel;

public interface IDelOutboundRetryLabelService extends IService<DelOutboundRetryLabel> {

    void save(String orderNo);

    void saveAndPushLabel(String orderNo);
}
