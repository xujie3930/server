package com.szmsd.http.quartz;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.TpieceDto;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.service.IRetreatPieceService;
import com.szmsd.http.service.RemoteInterfaceService;
import com.szmsd.http.vo.HttpResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static sun.security.krb5.internal.KerberosTime.now;


public class TjJob extends QuartzJobBean {

    private final Logger logger = LoggerFactory.getLogger(TjJob.class);

    @Autowired

    private RemoteInterfaceService remoteInterfaceService;

    @Autowired
    private IRetreatPieceService iRetreatPieceService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        TpieceDto tpieceDto=new TpieceDto();
        HttpRequestDto httpRequestDto = new HttpRequestDto();
//        tpieceDto.setLimit(100);
//        tpieceDto.setOffset(50);
        tpieceDto.setHash(false);
        //获取当前天的开始时间
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //前一天的开始时间
        cal.add(Calendar.DAY_OF_MONTH, -1);
        tpieceDto.setFrom(cal.getTime());
        //结束时间 就是定时任务刷的当前时间
        Date dt = new Date();
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.HOUR, +1);
        Date dt1=rightNow.getTime();

        tpieceDto.setTo(dt1);
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.TJAPIDomain.wrapper("/api/reception/finished/events");
        httpRequestDto.setUri(url);
        httpRequestDto.setBody(tpieceDto);
        HttpResponseVO httpResponseVO = remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();
        logger.info("调用国外接口返回的Body：{}",o);
        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);
        Object value = JSONObject.toJSON(map4.get("result"));
        logger.info("调用国外接口返回的result：{}",value);
        Map map5 = JSONObject.parseObject(value.toString(), Map.class);
        logger.info("调用国外接口返回的map5：{}",map5);
        int a= iRetreatPieceService.insetRetreatPiece(map5);

    }

//    public static void main(String[] args) throws ParseException {
//
//        Date dt = new Date();
//
//        SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
//        Date date = sdf.parse( "2022-09-19 23:00:00" );
//        Calendar rightNow = Calendar.getInstance();
//        rightNow.setTime(dt);
//        //rightNow.add(Calendar.DATE, -1);
//        rightNow.add(Calendar.HOUR, +1);
//        Date dt1=rightNow.getTime();
//
//        System.out.println(dt1);
//
//        //前一天的开始时间
//        Calendar cal = new GregorianCalendar();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        cal.add(Calendar.DAY_OF_MONTH, -1);
//        System.out.println(cal.getTime());
//
//    }

//    public static void main(String[] args) {
//        Calendar cal = new GregorianCalendar();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        System.out.println(cal.getTime());
//    }

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
