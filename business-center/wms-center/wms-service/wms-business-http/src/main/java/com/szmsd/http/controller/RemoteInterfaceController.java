package com.szmsd.http.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.HttpRequestSyncDTO;
import com.szmsd.http.dto.TpieceDto;
import com.szmsd.http.dto.TpieceVO;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.service.IRemoteExecutorTask;
import com.szmsd.http.service.RemoteInterfaceService;
import com.szmsd.http.vo.HttpResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;

@Api(tags = {"HTTP调用接口"})
@ApiSort(10000)
@RestController
@RequestMapping("/api/rmi")
public class RemoteInterfaceController extends BaseController {

    @Autowired
    private RemoteInterfaceService remoteInterfaceService;
    @Resource
    private IRemoteExecutorTask iRemoteExecutorTask;

    @PostMapping
    @ApiOperation(value = "HTTP调用接口 - #1", position = 100)
    @ApiImplicitParam(name = "dto", value = "dto", dataType = "HttpRequestDto")
    public R<HttpResponseVO> rmi(@RequestBody @Validated HttpRequestDto dto) {
        return R.ok(remoteInterfaceService.rmi(dto));
    }

    //测试第三方调用
    @PostMapping("testRmi")
    @ApiOperation(value = "HTTP调用接口第三方白名单 - #1", position = 100)
    @ApiImplicitParam(name = "dto", value = "dto", dataType = "TpieceDto")
    public R<Map> testRmi(@RequestBody TpieceDto tpieceDto) throws IllegalAccessException {
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        //tpieceDto.setLimit(100);
        //tpieceDto.setOffset(50);
        //tpieceDto.setHash(false);
        //获取当前天的开始时间
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        //tpieceDto.setFrom(cal.getTime());
        //结束时间 就是定时任务刷的当前时间
        //tpieceDto.setTo(new Date());
        httpRequestDto.setMethod(HttpMethod.GET);
        String url = DomainEnum.TJAPIDomain.wrapper("/api/reception/finished/events");
        httpRequestDto.setUri(url);
        httpRequestDto.setBody(tpieceDto);
        HttpResponseVO httpResponseVO=remoteInterfaceService.rmi(httpRequestDto);
        Object o=httpResponseVO.getBody();

        Map map3 = objectToMap(o);
         Map map=new HashMap();
         map.put("boby",map3);
         String result=String.valueOf(map3.get("result"));
        String events=String.valueOf(map3.get("events"));
        String products=String.valueOf(map3.get("products"));
        map.put("result",result);
        map.put("events",events);
        map.put("products",products);

        Map map4 = JSONObject.parseObject(String.valueOf(o), Map.class);

        Object value = JSONObject.toJSON(map4.get("result"));

        Map map5 = JSONObject.parseObject(value.toString(), Map.class);
        map.put("map5",map5);
        map.put("boby2",httpResponseVO.getBody());
        return R.ok(map);
    }

    public  Map objectToMap(Object obj) throws IllegalAccessException {
        Map map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        System.out.println(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            map.put(fieldName, value);
        }
        return map;
    }




//    public static void main(String[] args) {
//        HttpRequestDto httpRequestDto = new HttpRequestDto();
//
//        Map map2=new HashMap();
//
//        Map map=new HashMap();
//        map.put("partner_code","TST2");
//        map.put("hash","00000000");
//        map.put("job","sdls_jb_CtRDu40Qli9PG6Lg1cOoXdfkovb4");
//        List<Map> list=new ArrayList<>();
//        list.add(map);
//        map.put("ss",list);
//        map2.put("result",map);
//        httpRequestDto.setBody(map2);
//        Map map1= (Map) ((HashMap) httpRequestDto.getBody()).get("result");
//        TpieceVO tpieceVO= JSON.parseObject(JSON.toJSONString(((HashMap) httpRequestDto.getBody()).get("result")),TpieceVO.class);
//        Map map3 = JSONObject.toJavaObject(JSONObject.parseObject(JSONObject.toJSONString(httpRequestDto.getBody())), Map.class);
//         String a="{\"result\":{\"partner_code\":\"TST2\",\"events\":[{\"reception_service\":\"SUPPLIER\",\"reception_wms_id\":57455,\"date_finished\":\"2021-10-12T10:52:41.403Z\",\"products\":{\"125751\":{\"code\":\"10001\",\"um\":\"Buc\",\"ua\":\"\",\"qty_received\":5}}}]},\"job\":\"sdls_jb_87Njq1ouf0v9wglKpiDaMa7MhPK5\",\"success\":true,\"status_code\":200}";
//        Map map4 = JSONObject.parseObject(a, Map.class);
//
//        Object value = JSONObject.toJSON(map4.get("result"));
//
//        Map map5 = JSONObject.parseObject(value.toString(), Map.class);
//        System.out.println(map5);
//
//    }
    @PostMapping(value = "sync")
    @ApiOperation(value = "HTTP调用接口 - #1", position = 100)
    @ApiImplicitParam(name = "dto", value = "dto", dataType = "HttpRequestDto")
    public R<HttpResponseVO> rmiSync(@RequestBody @Validated HttpRequestSyncDTO dto) {
        remoteInterfaceService.rmiSync(dto);
        return R.ok();
    }

//    public static void main(String[] args) {
//        HttpRequestDto httpRequestDto = new HttpRequestDto();
//
//        Map map2=new HashMap();
//
//        Map map=new HashMap();
//        map.put("partner_code","TST2");
//        map.put("hash","00000000");
//        map.put("job","sdls_jb_CtRDu40Qli9PG6Lg1cOoXdfkovb4");
//        map2.put("result",map);
//        httpRequestDto.setBody(map2);
//        Map map1= (Map) ((HashMap) httpRequestDto.getBody()).get("result");
//        TpieceVO tpieceVO= JSON.parseObject(JSON.toJSONString(((HashMap) httpRequestDto.getBody()).get("result")),TpieceVO.class);
//                System.out.println(tpieceVO);
//
//    }


    @PreAuthorize("@ss.hasPermi('CommonScan:CommonScan:executeTask')")
    @Log(title = "扫描JOB执行任务模块", businessType = BusinessType.INSERT)
    @PostMapping("/executeTask")
    @ApiOperation(value = "定时任务扫描", notes = "定时任务扫描")
    public R executeTask() {
        iRemoteExecutorTask.executeTask();
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('CommonScan:CommonScan:syncFinishScanDate')")
    @Log(title = "扫描JOB执行任务模块", businessType = BusinessType.INSERT)
    @PostMapping("/syncFinishScanDate")
    @ApiOperation(value = "定时任务数据迁移", notes = "定时任务数据迁移")
    public R syncFinishScanDate() {
        iRemoteExecutorTask.syncFinishScanDate();
        return R.ok();
    }
}
