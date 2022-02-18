package com.szmsd.delivery.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.TrackAnalysisDto;
import com.szmsd.delivery.dto.TrackAnalysisRequestDto;
import com.szmsd.delivery.dto.TrackingYeeTraceDto;
import com.szmsd.delivery.util.SHA256Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.delivery.service.IDelTrackService;
import com.szmsd.delivery.domain.DelTrack;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;

import java.util.LinkedHashMap;
import java.util.List;
import java.io.IOException;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */


@Api(tags = {""})
@RestController
@RequestMapping("/del-track")
public class DelTrackController extends BaseController {

    @Resource
    private IDelTrackService delTrackService;

    @Value("${webhook.secret}")
    private String webhookSecret;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    @AutoValue
    public TableDataInfo list(DelTrack delTrack) {
        startPage();
        List<DelTrack> list = delTrackService.selectDelTrackList(delTrack);
        return getDataTable(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void export(HttpServletResponse response, DelTrack delTrack) throws IOException {
        List<DelTrack> list = delTrackService.selectDelTrackList(delTrack);
        ExcelUtil<DelTrack> util = new ExcelUtil<DelTrack>(DelTrack.class);
        util.exportExcel(response, list, "DelTrack");

    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块", notes = "删除模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delTrackService.deleteDelTrackByIds(ids));
    }


    @ApiOperation(value = "用于接收TrackingYee回调的路由信息", notes = "")
    @PostMapping("/traceCallback")
    public R traceCallback(HttpServletRequest request, @RequestBody Object params){
        if(params == null) {
            return R.failed("非法请求，参数异常！");
        }
        // 验证签名
        String trackingyeeSign = request.getHeader("trackingyee-webhook-signature");
        String requestStr = JSONObject.toJSONString(params, SerializerFeature.WriteMapNullValue);
        String verifySign = SHA256Util.getSHA256Str(webhookSecret + requestStr);
        log.info("trackingyeeSign: {}", trackingyeeSign);
        log.info("待加密验签字符串: {}", webhookSecret + requestStr);
        log.info("verifySign: {}", verifySign);
        TrackingYeeTraceDto trackingYeeTraceDto = JSONObject.parseObject(requestStr, TrackingYeeTraceDto.class);
        if (StringUtils.isBlank(trackingyeeSign) || !trackingyeeSign.equalsIgnoreCase(verifySign)) {
            return R.failed("非法请求，验签失败！");
        }
        if (trackingYeeTraceDto == null) {
            return R.failed("非法请求，参数异常！");
        }
        // 处理数据
        delTrackService.traceCallback(trackingYeeTraceDto);
        return R.ok();
    }

    @ApiOperation(value = "获取轨迹分析", notes = "获取轨迹分析")
    @GetMapping("getTrackAnalysis")
    public R getTrackAnalysis(TrackAnalysisRequestDto requestDto){
        return R.ok(delTrackService.getTrackAnalysis(requestDto));
    }


    @ApiOperation(value = "获取轨迹状态下的各个发货服务订单量分析", notes = "获取轨迹状态下的各个发货服务订单量分析")
    @GetMapping("getProductAnalysis")
    public R getProductAnalysis(TrackAnalysisRequestDto requestDto){
        return R.ok(delTrackService.getProductServiceAnalysis(requestDto));
    }

}
