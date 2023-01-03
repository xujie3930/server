package com.szmsd.bas.quartz;

import com.szmsd.bas.domain.BasTransportConfig;
import com.szmsd.bas.mapper.BasTransportConfigMapper;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.http.api.feign.YcMeetingFeignService;
import com.szmsd.http.domain.YcAppParameter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BasTransportConfigJob  extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        BasTransportConfigMapper basTransportConfigMapper= SpringUtils.getBean(BasTransportConfigMapper.class);
        YcMeetingFeignService ycMeetingFeignService= SpringUtils.getBean(YcMeetingFeignService.class);
        List<YcAppParameter> list=ycMeetingFeignService.selectBasYcappConfig().getData();
        list.forEach(x->{
            x.setService("getShippingMethod");
            Map map=ycMeetingFeignService.YcApiri(x).getData();
            List<Map> list1= (List<Map>) map.get("data");
            list1.forEach(s->{
                BasTransportConfig basTransportConfig=new BasTransportConfig();
                basTransportConfig.setTransportCode(String.valueOf(s.get("code")));
                basTransportConfig.setTransportName(String.valueOf(s.get("name")));
                basTransportConfig.setTransportNameEn(String.valueOf(s.get("name_en")));
                basTransportConfig.setWarehouseCode(String.valueOf(s.get("warehouse_code")));
                List<BasTransportConfig> list2=basTransportConfigMapper.selectListVO(basTransportConfig);
                if (list2.size()>0){
                    list2.forEach(q->{
                        basTransportConfigMapper.deleteByPrimaryKey(String.valueOf(s.get("code")));
                    });
                }

                basTransportConfig.setCreateBy("admin");
                basTransportConfig.setCreateTime(new Date());
                basTransportConfig.setCreateByName("admin系统自动创建");
                basTransportConfigMapper.insertSelective(basTransportConfig);

            });
        });

    }

}
