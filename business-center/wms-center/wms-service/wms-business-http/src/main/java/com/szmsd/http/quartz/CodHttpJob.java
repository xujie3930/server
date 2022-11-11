package com.szmsd.http.quartz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.http.domain.BasCodExternal;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.TpieceDto;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.mapper.BasCodExternalMapper;
import com.szmsd.http.service.RemoteInterfaceService;
import com.szmsd.http.vo.HttpResponseVO;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.quartz.QuartzJobBean;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CodHttpJob extends QuartzJobBean {

    @Autowired
    private RemoteInterfaceService remoteInterfaceService;

    @Autowired
    private BasCodExternalMapper basCodExternalMapper;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(HttpMethod.GET);
        basCodExternalMapper.deleteByPrimaryKey();
         int i=1;
        while (i>0){
            String url = DomainEnum.Ck1OpenAPIDomain.wrapper("/v1/Bills/ExchangeRate?PageSize=200&PageIndex="+i+"");
            i++;
            httpRequestDto.setUri(url);
            //httpRequestDto.setBody(tpieceDto);
            HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
            Object o=httpResponseVO.getBody();
            Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);

            List<BasCodExternal> basCodExternal =JSONArray.parseArray(map4.get("Data").toString(),BasCodExternal.class);
            if (basCodExternal.size()==0){
                break;
            }
            basCodExternal.forEach(x->{
                String updatedTime=x.getUpdatedTime();
                String updatedTimes=updatedTime.substring(6, 19);
                Long updatedTimeslong=Long.valueOf(updatedTimes);
                Date date = new Date();
                date.setTime(updatedTimeslong);
                x.setUpdatedTimes(date);
                basCodExternalMapper.insertSelective(x);
            });
        }


    }




}
