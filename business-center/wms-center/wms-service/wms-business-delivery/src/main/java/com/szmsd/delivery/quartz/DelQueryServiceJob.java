package com.szmsd.delivery.quartz;

import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelQueryService;
import com.szmsd.delivery.mapper.DelQueryServiceMapper;
import com.szmsd.delivery.service.IDelQueryServiceService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 查件服务 定时任务处理
 * </p>
 *
 * @author jun
 * @since 2022-09-16
 */
@Slf4j
public class DelQueryServiceJob extends QuartzJobBean {

    @Autowired
    private IDelQueryServiceService delQueryServiceService;
    @Autowired
    private DelQueryServiceMapper delQueryServiceMapper;

    @Resource
    private BasSellerFeignService basSellerFeignService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        String startTime = DateUtils.getPastDate(60,DateUtils.YYYY_MM_DD_HH_MM_SS);
        String endTime = DateUtils.dateTimeStr(new Date());

        log.info("executeInternal 时间查询范围:{},{}",startTime,endTime);

      List<DelOutbound> delOutboundList=delQueryServiceMapper.selectDelOutbound(startTime,endTime);

      List<DelQueryService> delQueryServiceList=new ArrayList<>();
        delOutboundList.forEach(x->{
            DelQueryService delQueryService=new DelQueryService();
            BeanUtils.copyProperties(x,delQueryService);
            delQueryService.setTraceId(x.getTrackingNo());
            R<BasSellerInfoVO> info = basSellerFeignService.getInfoBySellerCode(x.getSellerCode());
            if(info.getData() != null){
                BasSellerInfoVO userInfo = R.getDataAndException(info);
                delQueryService.setServiceStaff(userInfo.getServiceStaff());
                delQueryService.setServiceStaffName(userInfo.getServiceStaffName());
                delQueryService.setServiceStaffNickName(userInfo.getServiceStaffNickName());

                delQueryService.setServiceManagerName(userInfo.getServiceManagerName());
                delQueryService.setServiceManagerNickName(userInfo.getServiceManagerNickName());
                delQueryService.setServiceManager(userInfo.getServiceManager());

            }
            delQueryService.setShipmentService(x.getShipmentRule());
            delQueryServiceList.add(delQueryService);
        });
        delQueryServiceService.insertDelQueryServiceList(delQueryServiceList);

    }
}
