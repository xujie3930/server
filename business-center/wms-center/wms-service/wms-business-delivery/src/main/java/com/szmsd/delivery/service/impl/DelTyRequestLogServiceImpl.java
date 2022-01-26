package com.szmsd.delivery.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelTyRequestLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.mapper.DelTyRequestLogMapper;
import com.szmsd.delivery.service.IDelTyRequestLogService;
import com.szmsd.http.api.service.IHtpRmiClientService;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;
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
public class DelTyRequestLogServiceImpl extends ServiceImpl<DelTyRequestLogMapper, DelTyRequestLog> implements IDelTyRequestLogService {
    private final Logger logger = LoggerFactory.getLogger(DelTyRequestLogServiceImpl.class);

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IHtpRmiClientService htpRmiClientService;
    //                                            0   1   2   3   4   5   6   7   8    9    10   11
    private final int[] retryTimeConfiguration = {30, 30, 60, 60, 60, 60, 60, 60, 180, 180, 180, 180};
    public static final int retryCount = 10;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_TY_REQUEST)
    @Override
    public void handler(DelTyRequestLog tyRequestLog) {
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
                if (success) {
                    state = DelCk1RequestLogConstant.State.SUCCESS.name();
                } else {
                    failCount++;
                    if (failCount >= retryCount) {
                        state = DelCk1RequestLogConstant.State.FAIL.name();
                    } else {
                        state = DelCk1RequestLogConstant.State.FAIL_CONTINUE.name();
                        int t = retryTimeConfiguration[failCount];
                        nextRetryTime = DateUtils.addSeconds(tyRequestLog.getNextRetryTime(), t);
                    }
                }
                int lastRequestConsumeTime = (int) (System.currentTimeMillis() - st);
                LambdaUpdateWrapper<DelTyRequestLog> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.set(DelTyRequestLog::getState, state);
                updateWrapper.set(DelTyRequestLog::getFailCount, failCount);
                updateWrapper.set(DelTyRequestLog::getResponseBody, responseBody);
                updateWrapper.set(DelTyRequestLog::getLastRequestConsumeTime, lastRequestConsumeTime);
                updateWrapper.set(DelTyRequestLog::getNextRetryTime, nextRetryTime);
                updateWrapper.eq(DelTyRequestLog::getId, tyRequestLog.getId());
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
