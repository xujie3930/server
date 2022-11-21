package com.szmsd.bas.api.service.impl;

import com.szmsd.bas.api.feign.SerialNumberFeignService;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.dto.GenerateNumberDto;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyuyuan
 * @date 2020-12-02 002 9:42
 */
@Service
public class SerialNumberClientServiceImpl implements SerialNumberClientService {

    @Autowired
    private SerialNumberFeignService serialNumberFeignService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String generateNumber(String code) {
        GenerateNumberDto dto = new GenerateNumberDto();
        dto.setCode(code);
        R<String> r = this.serialNumberFeignService.generateNumber(dto);
        if (null == r || Constants.SUCCESS != r.getCode()) {
            throw new CommonException("999", "生成流水号失败");
        }
        return r.getData();
    }

    @Override
    public String generatorNumber(String code) {

        return this.createNo(code);
    }

    @Override
    public List<String> generateNumbers(String code, int num) {
        GenerateNumberDto dto = new GenerateNumberDto();
        dto.setCode(code);
        dto.setNum(num);
        R<List<String>> listR = this.serialNumberFeignService.generateNumbers(dto);
        if (null == listR || Constants.SUCCESS != listR.getCode()) {
            throw new CommonException("999", "生成流水号失败");
        }
        return listR.getData();
    }

    private String createNo(String cusCode) {

        StringBuilder sb = new StringBuilder().append(cusCode);
        String keyPrefix = sb.toString();

        RedisAtomicInteger atomicInteger = new RedisAtomicInteger(keyPrefix,redisTemplate.getConnectionFactory());

        Integer no = atomicInteger.incrementAndGet();

        Long expiresTime = this.getSecondsNextEarlyMorning();
        atomicInteger.expire(expiresTime, TimeUnit.SECONDS);

        String length = String.format("%08d", no);

        String currentCycle = DateFormatUtils.format(new Date(), "yyMMdd");

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(currentCycle);
        resultBuilder.append(length);

        return resultBuilder.toString();
    }

    /**
     * 判断当前时间距离第二天凌晨的秒数
     *
     * @return 返回值单位为[s:秒]
     */
    public Long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }
}
