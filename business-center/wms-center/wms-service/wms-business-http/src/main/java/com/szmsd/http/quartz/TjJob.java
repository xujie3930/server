package com.szmsd.http.quartz;

import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.TpieceDto;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.service.RemoteInterfaceService;
import com.szmsd.http.vo.HttpResponseVO;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static sun.security.krb5.internal.KerberosTime.now;


public class TjJob extends QuartzJobBean {

    private final Logger logger = LoggerFactory.getLogger(TjJob.class);

    @Autowired

    private RemoteInterfaceService remoteInterfaceService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        TpieceDto tpieceDto=new TpieceDto();
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        tpieceDto.setLimit(100);
        tpieceDto.setOffset(50);
        tpieceDto.setHash(true);
        //获取当前天的开始时间
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        tpieceDto.setStartDate(cal.getTime());
        //结束时间 就是定时任务刷的当前时间
        tpieceDto.setEndDate(new Date());
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.TJAPIDomain.wrapper("/api/reception/finished/events");
        httpRequestDto.setUri(url);
        httpRequestDto.setBody(tpieceDto);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);


    }

//    public static long getTime(int dayOffset){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.DATE,dayOffset);
//        Date time = calendar.getTime();
//        String format = sdf1.format(time);
//        String start = format.concat(" 00:00:01");
//        String end =  format.concat(" 23:59:59");
//        try {
//            Date parse = sdf.parse(start);
//            String format1 = sdf.format(parse);
//            System.out.println("某一天的开始："+format1);
//            Date end1 = sdf.parse(end);
//            String end12 = sdf.format(end1);
//            System.out.println("某一天的结束："+end12);
//            System.out.println("某一天的结束秒："+end1.getTime()/1000);
//            System.out.println("某一天的开始秒："+parse.getTime()/1000);
//            return parse.getTime()/1000;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return now().getTime()/1000;
//    }



}
