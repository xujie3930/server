package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelCk1RequestLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.mapper.DelCk1RequestLogMapper;
import com.szmsd.delivery.service.IDelCk1RequestLogService;
import com.szmsd.http.api.service.IHtpRmiClientService;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;
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
public class DelCk1RequestLogServiceImpl extends ServiceImpl<DelCk1RequestLogMapper, DelCk1RequestLog> implements IDelCk1RequestLogService {
    private final Logger logger = LoggerFactory.getLogger(DelCk1RequestLogServiceImpl.class);

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IHtpRmiClientService htpRmiClientService;
    //                                            0   1   2   3   4   5   6   7   8    9    10   11
    private final int[] retryTimeConfiguration = {30, 30, 60, 60, 60, 60, 60, 60, 180, 180, 180, 180};
    public static final int retryCount = 10;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_CK1_REQUEST)
    @Override
    public void handler(DelCk1RequestLog ck1RequestLog) {
        Long id = ck1RequestLog.getId();
        String lockName = applicationName + ":DelCk1RequestLogServiceImpl:" + id;
        RLock lock = redissonClient.getLock(lockName);
        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                String responseBody;
                int failCount = ck1RequestLog.getFailCount();
                String state;
                long st = System.currentTimeMillis();
                Date nextRetryTime = null;
                boolean success = false;
                try {
                    HttpRequestDto httpRequestDto = new HttpRequestDto();
                    String type = ck1RequestLog.getType();
                    HttpMethod method;
                    if (DelCk1RequestLogConstant.Type.cancel.name().equals(type)
                            || DelCk1RequestLogConstant.Type.finished.name().equals(type)) {
                        method = HttpMethod.PUT;
                    } else {
                        method = HttpMethod.POST;
                    }
                    httpRequestDto.setMethod(method);
                    httpRequestDto.setUri(ck1RequestLog.getUrl());
                    httpRequestDto.setBody(ck1RequestLog.getRequestBody());
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
                        nextRetryTime = DateUtils.addSeconds(ck1RequestLog.getNextRetryTime(), t);
                    }
                }
                int lastRequestConsumeTime = (int) (System.currentTimeMillis() - st);
                LambdaUpdateWrapper<DelCk1RequestLog> updateWrapper = Wrappers.lambdaUpdate();
                updateWrapper.set(DelCk1RequestLog::getState, state);
                updateWrapper.set(DelCk1RequestLog::getFailCount, failCount);
                updateWrapper.set(DelCk1RequestLog::getResponseBody, responseBody);
                updateWrapper.set(DelCk1RequestLog::getLastRequestConsumeTime, lastRequestConsumeTime);
                updateWrapper.set(DelCk1RequestLog::getNextRetryTime, nextRetryTime);
                updateWrapper.eq(DelCk1RequestLog::getId, ck1RequestLog.getId());
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
