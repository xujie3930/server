package com.szmsd.track.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.vo.DelOutboundTrackRequestVO;
import com.szmsd.http.api.service.IHtpRmiClientService;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.track.config.ThreadPoolExecutorConfiguration;
import com.szmsd.track.domain.TrackTyRequestLog;
import com.szmsd.track.enums.TrackTyRequestLogConstant;
import com.szmsd.track.mapper.DelTyRequestLogMapper;
import com.szmsd.track.service.IDelTyRequestLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class DelTyRequestLogServiceImpl extends ServiceImpl<DelTyRequestLogMapper, TrackTyRequestLog> implements IDelTyRequestLogService {
    private final Logger logger = LoggerFactory.getLogger(DelTyRequestLogServiceImpl.class);

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IHtpRmiClientService htpRmiClientService;

    @Autowired
    private DelOutboundFeignService delOutboundFeignService;
    //                                            0   1   2   3   4   5   6   7   8    9    10   11
    private final int[] retryTimeConfiguration = {30, 30, 60, 60, 60, 60, 60, 60, 180, 180, 180, 180};
    public static final int retryCount = 10;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_TY_REQUEST)
    @Override
    public void handler(TrackTyRequestLog tyRequestLog) {
        Long id = tyRequestLog.getId();
        String lockName = applicationName + ":DelTyRequestLogServiceImpl:" + id;
        RLock lock = redissonClient.getLock(lockName);
        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                String responseBody;
                int failCount = tyRequestLog.getFailCount();
                String state;
                long st = System.currentTimeMillis();
                Date nextRetryTime = null;
                boolean success = false;
                try {
                    HttpRequestDto httpRequestDto = new HttpRequestDto();
                    httpRequestDto.setMethod(HttpMethod.valueOf(tyRequestLog.getMethod()));
                    httpRequestDto.setUri(tyRequestLog.getUrl());
                    String requestBody = tyRequestLog.getRequestBody();
                    if (StringUtils.isNotEmpty(requestBody)) {
                        Object body;
                        try {
                            body = JSON.parse(requestBody);
                        } catch (Exception e) {
                            body = requestBody;
                        }
                        httpRequestDto.setBody(body);
                    } else {
                        httpRequestDto.setBody(requestBody);
                    }
                    HttpResponseVO httpResponseVO = htpRmiClientService.rmi(httpRequestDto);
                    if (200 == httpResponseVO.getStatus() ||
                            201 == httpResponseVO.getStatus()) {
                        success = true;
                    }
                    responseBody = (String) httpResponseVO.getBody();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    responseBody = e.getMessage();
                    if (null == responseBody) {
                        responseBody = "请求失败";
                    }
                }
                // 请求成功，解析响应报文
                if (success) {
                    try {
                        // 解析响应报文，获取响应参数信息
                        JSONObject jsonObject = JSON.parseObject(responseBody);
                        // 判断状态是否为OK
                        if ("OK".equals(jsonObject.getString("status"))) {
                            // 判断结果明细是不是成功的
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (1 == data.getIntValue("successNumber")) {
                                JSONArray successImportRowResults = data.getJSONArray("successImportRowResults");
                                JSONObject successImportRowResult = successImportRowResults.getJSONObject(0);
                                String tyShipmentId = successImportRowResult.getString("id");
                                if (StringUtils.isNotEmpty(tyShipmentId)) {
                                    // 回写到出库表上
                                    DelOutboundTrackRequestVO delOutboundTrackRequestVO = new DelOutboundTrackRequestVO();
                                    delOutboundTrackRequestVO.setTyShipmentId(tyShipmentId);
                                    delOutboundTrackRequestVO.setOrderNo(tyRequestLog.getOrderNo());
                                    delOutboundFeignService.updateDeloutboundTrackMsg(delOutboundTrackRequestVO);
                                }
                            } else {
                                // 返回的成功数量不是1，判定为异常
                                success = false;
                            }
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        // 解析失败，判定为异常
                        success = false;
                    }
                }
                if (success) {
                    state = TrackTyRequestLogConstant.State.SUCCESS.name();
                } else {
                    failCount++;
                    if (failCount >= retryCount) {
                        state = TrackTyRequestLogConstant.State.FAIL.name();
                    } else {
                        state = TrackTyRequestLogConstant.State.FAIL_CONTINUE.name();
                        int t = retryTimeConfiguration[failCount];
                        nextRetryTime = DateUtils.addSeconds(tyRequestLog.getNextRetryTime(), t);
                    }
                }
                int lastRequestConsumeTime = (int) (System.currentTimeMillis() - st);
                LambdaUpdateWrapper<TrackTyRequestLog> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.set(TrackTyRequestLog::getState, state);
                updateWrapper.set(TrackTyRequestLog::getFailCount, failCount);
                updateWrapper.set(TrackTyRequestLog::getResponseBody, responseBody);
                updateWrapper.set(TrackTyRequestLog::getLastRequestConsumeTime, lastRequestConsumeTime);
                updateWrapper.set(TrackTyRequestLog::getNextRetryTime, nextRetryTime);
                updateWrapper.eq(TrackTyRequestLog::getId, tyRequestLog.getId());
                super.update(updateWrapper);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
