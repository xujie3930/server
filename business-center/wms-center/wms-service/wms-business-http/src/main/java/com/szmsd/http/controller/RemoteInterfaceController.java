package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.HttpRequestSyncDTO;
import com.szmsd.http.dto.TpieceDto;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.service.IRemoteExecutorTask;
import com.szmsd.http.service.RemoteInterfaceService;
import com.szmsd.http.vo.HttpResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    @ApiImplicitParam(name = "dto", value = "dto", dataType = "HttpRequestDto")
    public R<HttpResponseVO> testRmi(@RequestBody @Validated HttpRequestDto dto) {
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
        return R.ok(remoteInterfaceService.rmi(dto));
    }

    @PostMapping(value = "sync")
    @ApiOperation(value = "HTTP调用接口 - #1", position = 100)
    @ApiImplicitParam(name = "dto", value = "dto", dataType = "HttpRequestDto")
    public R<HttpResponseVO> rmiSync(@RequestBody @Validated HttpRequestSyncDTO dto) {
        remoteInterfaceService.rmiSync(dto);
        return R.ok();
    }


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
