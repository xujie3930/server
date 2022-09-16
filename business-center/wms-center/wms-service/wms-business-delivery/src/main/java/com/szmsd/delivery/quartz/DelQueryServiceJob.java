package com.szmsd.delivery.quartz;

import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelQueryService;
import com.szmsd.delivery.mapper.DelQueryServiceMapper;
import com.szmsd.delivery.service.IDelQueryServiceService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 查件服务 定时任务处理
 * </p>
 *
 * @author jun
 * @since 2022-09-16
 */
public class DelQueryServiceJob extends QuartzJobBean {

    @Autowired
    private IDelQueryServiceService delQueryServiceService;
    @Autowired
    private DelQueryServiceMapper delQueryServiceMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      List<DelOutbound> delOutboundList=delQueryServiceMapper.selectDelOutbound();
      List<DelQueryService> delQueryServiceList=new ArrayList<>();
        delOutboundList.forEach(x->{
            DelQueryService delQueryService=new DelQueryService();
            BeanUtils.copyProperties(x,delQueryService);
            delQueryServiceList.add(delQueryService);
        });
        delQueryServiceService.insertDelQueryServiceList(delQueryServiceList);

    }
}
