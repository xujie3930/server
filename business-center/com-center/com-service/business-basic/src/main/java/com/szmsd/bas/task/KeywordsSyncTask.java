package com.szmsd.bas.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.bas.domain.BasCarrierKeyword;
import com.szmsd.bas.keyword.KeywordsUtil;
import com.szmsd.bas.service.IBasCarrierKeywordService;
import com.szmsd.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 关键词同步redis
 */
@Component
@Slf4j
public class KeywordsSyncTask {

    @Autowired
    private IBasCarrierKeywordService carrierKeywordService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置10 分钟执行执行一次
     */
    @Scheduled(fixedDelay = 10*60*1000)
    public void sync(){
        this.sync(null);
    }

    public void sync(String carrierCode){
        log.info("================开始关键词同步至Redis===================");
        List<BasCarrierKeyword> carrierKeywords = carrierKeywordService.list(new LambdaQueryWrapper<BasCarrierKeyword>().eq(StringUtils.isNotBlank(carrierCode), BasCarrierKeyword::getCarrierCode, carrierCode));
        ValueOperations operations = redisTemplate.opsForValue();
        if (CollectionUtils.isEmpty(carrierKeywords)) {
            return;
        }
        Map<String, List<BasCarrierKeyword>> carrierKeywordsMap = carrierKeywords.stream().collect(Collectors.groupingBy(BasCarrierKeyword::getCarrierCode));
        carrierKeywordsMap.forEach((k, v) -> operations.set(KeywordsUtil.KEYWORD_KEY + k, JSON.toJSONString(v)));
        log.info("================结束关键词同步至Redis===================");
    }

}
