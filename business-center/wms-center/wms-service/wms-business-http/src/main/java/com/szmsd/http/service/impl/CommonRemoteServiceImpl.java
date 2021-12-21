package com.szmsd.http.service.impl;

import com.szmsd.http.vo.HttpResponseVO;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.szmsd.http.domain.CommonRemote;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.HttpRequestSyncDTO;
import com.szmsd.http.enums.RemoteConstant;
import com.szmsd.http.mapper.CommonScanMapper;
import com.szmsd.http.service.ICommonRemoteService;
import com.szmsd.http.service.RemoteInterfaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;

import static com.szmsd.http.enums.RemoteConstant.RemoteStatusEnum;
import static com.szmsd.http.enums.RemoteConstant.RemoteTypeEnum;

/**
 * <p>
 * 扫描执行任务 服务实现类
 * </p>
 *
 * @author huanggaosheng
 * @since 2021-11-10
 */
@Slf4j
@Service
public class CommonRemoteServiceImpl extends ServiceImpl<CommonScanMapper, CommonRemote> implements ICommonRemoteService {

    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private RemoteInterfaceService remoteInterfaceService;

    /**
     * 实际 单线程 执行任务
     *
     * @param oneTask
     */
    @Override
    public void doTask(CommonRemote oneTask) {
        if (null == oneTask) return;

        log.info("开始调用-{}", oneTask);
        Integer scanType = oneTask.getRemoteType();
        oneTask.setRequestStatus(RemoteConstant.RemoteStatusEnum.SUCCESS.getStatus());
        oneTask.setReRequestTime(LocalDateTime.now());
        oneTask.setRetryTimes(oneTask.getRetryTimes() + 1);
        oneTask.setErrorMsg("");
        HttpRequestDto httpRequestDto = new HttpRequestDto();
        httpRequestDto.setMethod(oneTask.getRequestMethod());
        httpRequestDto.setUri(oneTask.getRequestUri());
        HashMap<String, String> hashMap = JSONObject.parseObject(oneTask.getRequestHead(), HashMap.class);
        httpRequestDto.setHeaders(hashMap);
        httpRequestDto.setBody(JSONObject.parseObject(oneTask.getRequestParams()));
        httpRequestDto.setBinary(false);

        try {
            HttpResponseVO rmi = remoteInterfaceService.rmi(httpRequestDto);
            String errorMsg = rmi.getErrorMsg();
            log.info("【RMI】SYNC 开始调用-{}", httpRequestDto);
            if (StringUtils.isNotBlank(errorMsg)) {
                oneTask.setRequestStatus(RemoteStatusEnum.FAIL.getStatus());
                oneTask.setErrorMsg(errorMsg);
            } else {
                oneTask.setRequestStatus(RemoteStatusEnum.SUCCESS.getStatus());
            }
        } catch (Exception e) {
            log.error("推送失败请求参数：{}\n", oneTask, e);
            e.printStackTrace();
            oneTask.setRequestStatus(RemoteStatusEnum.FAIL.getStatus());
            String s = "" + e.toString();
            String errorMsg = s.substring(0, Math.min(1000, s.length()));
            oneTask.setRemark(errorMsg);
        } finally {
            log.info("执行完成更新状态--{}", oneTask);
            oneTask.setReResponseTime(LocalDateTime.now());
            try {
                this.updateById(oneTask);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("更新异常：{}", oneTask, e);
            }
        }
    }

    @Override
    public CommonRemote getOneTask(Integer id, RemoteTypeEnum remoteTypeEnum) {
        return baseMapper.selectOne(Wrappers.<CommonRemote>lambdaQuery().lt(CommonRemote::getRetryTimes, 3)
                .in(CommonRemote::getRequestStatus, RemoteStatusEnum.WAIT.getStatus(), RemoteConstant.RemoteStatusEnum.FAIL.getStatus())
                .eq(remoteTypeEnum != null, CommonRemote::getRemoteType, remoteTypeEnum != null ? remoteTypeEnum.getTypeCode() : null)
                .ge(CommonRemote::getId, id).last("LIMIT 1"));
    }

    /***
     * 插入一条执行任务，等待异步扫描任务执行
     * @param dto
     */
    @Override
    public void insertRmiOne(HttpRequestSyncDTO dto) {
        log.info("【RMI-SYNC】接收参数：{}", dto);
        CommonRemote commonRemote = new CommonRemote();
        commonRemote.setRemoteType(dto.getRemoteTypeEnum().getTypeCode());
        Map<String, String> map = new HashMap<>(64);
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        commonRemote.setRequestToken(Strings.nullToEmpty(header));
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = httpServletRequest.getHeader(key);
            map.put(key, value);
        }
        commonRemote.setRealRequestHead(JSONObject.toJSONString(map));
        commonRemote.setRequestMethod(dto.getMethod());
        commonRemote.setRequestHead(JSONObject.toJSONString(dto.getHeaders()));
        commonRemote.setRequestTime(LocalDateTime.now());
        commonRemote.setRequestUri(dto.getUri());
        commonRemote.setRequestParams(JSONObject.toJSONString(dto.getBody()));
        commonRemote.setRequestStatus(RemoteStatusEnum.WAIT.getStatus());
        log.info("【RMI-SYNC】插入数据库：{}", commonRemote);
        baseMapper.insert(commonRemote);
    }

}

