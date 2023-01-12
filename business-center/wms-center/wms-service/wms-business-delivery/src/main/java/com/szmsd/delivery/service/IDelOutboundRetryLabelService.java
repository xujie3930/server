package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundRetryLabel;

public interface IDelOutboundRetryLabelService extends IService<DelOutboundRetryLabel> {

    void saveAndPushLabel(String orderNo, String pushLabel, String createBy);

    void saveAndPushLabel(String orderNo, String pushLabel,String state, String createBy);

}
