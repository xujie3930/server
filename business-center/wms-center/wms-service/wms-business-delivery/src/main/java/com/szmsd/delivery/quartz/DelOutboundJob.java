package com.szmsd.delivery.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.delivery.domain.DelTimeliness;
import com.szmsd.delivery.domain.DelTimelinessConfig;
import com.szmsd.delivery.mapper.DelTimelinessConfigMapper;
import com.szmsd.delivery.mapper.DelTimelinessMapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelOutboundJob extends QuartzJobBean {
    @Autowired
     private DelTimelinessMapper delTimelinessMapper;
    @Autowired
     private DelTimelinessConfigMapper delTimelinessConfigMapper;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
         List<DelTimelinessConfig> list=delTimelinessConfigMapper.selectByPrimaryKey();
         Map map=new HashMap();
         map.put("sectionBeginTime",list.get(0).getSectionBeginTime());
         map.put("sectionEndTime",list.get(0).getSectionEndTime());
         map.put("scopeOne",list.get(0).getSectionSky());
         map.put("scopeTwo",list.get(1).getSectionSky());
         map.put("scopeThree",list.get(2).getSectionSky());
         map.put("scopeFour",list.get(3).getSectionSky());
         map.put("scopeFive",list.get(4).getSectionSky());


        List<Map> list2 =delTimelinessMapper.selectDelOutboundes(map);
        list2.forEach(x->{
            DelTimeliness delTimeliness= JSON.parseObject(JSON.toJSONString(x),DelTimeliness.class);
            delTimeliness.setCreateTime(new Date());
            delTimelinessMapper.insertSelective(delTimeliness);
        });




    }



}
