package com.szmsd.finance.quartz;

import com.szmsd.finance.dto.RefundReviewDTO;
import com.szmsd.finance.enums.RefundStatusEnum;
import com.szmsd.finance.mapper.BasRefundRequestMapper;
import com.szmsd.finance.service.IRefundRequestService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

public class BasRefundRequestJob extends QuartzJobBean {

    @Autowired
    private BasRefundRequestMapper basRefundRequestMapper;
    @Autowired
    private IRefundRequestService iRefundRequestService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<String> ids =basRefundRequestMapper.selectFid();
        ids.forEach(x->{
            basRefundRequestMapper.deleteByPrimaryKey(Integer.parseInt(x));
        });
        RefundReviewDTO refundReviewDTO=new RefundReviewDTO();
        refundReviewDTO.setIdList(ids);
        refundReviewDTO.setStatus(RefundStatusEnum.valueOf("COMPLETE"));
        iRefundRequestService.approve(refundReviewDTO);


    }


}
