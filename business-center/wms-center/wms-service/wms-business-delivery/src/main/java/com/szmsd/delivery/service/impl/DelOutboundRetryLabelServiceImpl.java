package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.DelOutboundRetryLabel;
import com.szmsd.delivery.mapper.DelOutboundRetryLabelMapper;
import com.szmsd.delivery.service.IDelOutboundRetryLabelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class DelOutboundRetryLabelServiceImpl extends ServiceImpl<DelOutboundRetryLabelMapper, DelOutboundRetryLabel> implements IDelOutboundRetryLabelService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void save(String orderNo) {
        DelOutboundRetryLabel retryLabel = new DelOutboundRetryLabel();
        retryLabel.setOrderNo(orderNo);
        retryLabel.setNextRetryTime(new Date());
        this.save(retryLabel);
    }
}
