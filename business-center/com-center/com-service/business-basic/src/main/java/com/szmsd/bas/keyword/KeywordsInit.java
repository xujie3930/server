package com.szmsd.bas.keyword;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.domain.BasCarrierKeyword;
import com.szmsd.bas.task.KeywordsSyncTask;
import com.szmsd.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class KeywordsInit {

    /**
     * 关键词库
     */
    public Map keywordMap;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private KeywordsSyncTask keywordsSyncTask;

    /**
     * 初始化承运商关键词
     *
     * @return
     */
    public Map initKeyWord(String carrierCode) {
        try {
            Object keywordsObj = redisTemplate.opsForValue().get(KeywordsUtil.KEYWORD_KEY + carrierCode);
            if (keywordsObj == null) {
                // 尝试重试同步关键词一次
                keywordsSyncTask.sync(carrierCode);
                keywordsObj = redisTemplate.opsForValue().get(KeywordsUtil.KEYWORD_KEY + carrierCode);
                if (keywordsObj == null) {
                    return new ConcurrentHashMap<>();
                }
            }
            List<BasCarrierKeyword> keywordsList = JSONObject.parseArray(keywordsObj.toString(), BasCarrierKeyword.class);
            // 从关键词集合对象中取出关键词并封装到Set集合中
            Set<String> keyWordSet = new HashSet<String>();
            for (BasCarrierKeyword s : keywordsList) {
                String keywords = s.getKeywords().trim();
                if (StringUtils.isNotBlank(keywords)) {
                    // 根据逗号分割关键词
                    String[] keywordArr = keywords.split(",");
                    keyWordSet.addAll(Arrays.asList(keywordArr));
                }
            }
            // 将关键词库加入到HashMap中
            addKeywordToHashMap(keyWordSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keywordMap;
    }

    /**
     * 封装关键词库
     *
     * @param keyWordSet
     */
    @SuppressWarnings("rawtypes")
    private void addKeywordToHashMap(Set<String> keyWordSet) {
        // 初始化HashMap对象并控制容器的大小
        keywordMap = new ConcurrentHashMap(keyWordSet.size());
        // 关键词
        String key = null;
        // 用来按照相应的格式保存关键词库数据
        Map nowMap = null;
        // 用来辅助构建关键词库
        Map<String, String> newWorMap = null;
        // 使用一个迭代器来循环关键词集合
        Iterator<String> iterator = keyWordSet.iterator();
        while (iterator.hasNext()) {
            key = iterator.next();
            // 等于关键词库，HashMap对象在内存中占用的是同一个地址，所以此nowMap对象的变化，keywordMap对象也会跟着改变
            nowMap = keywordMap;
            for (int i = 0; i < key.length(); i++) {
                // 截取关键词当中的字，在关键词库中字为HashMap对象的Key键值
                char keyChar = key.charAt(i);

                // 判断这个字是否存在于关键词库中
                Object wordMap = nowMap.get(keyChar);
                if (wordMap != null) {
                    nowMap = (Map) wordMap;
                } else {
                    newWorMap = new HashMap<String, String>();
                    newWorMap.put("isEnd", "0");
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }
                // 如果该字是当前关键词的最后一个字，则标识为结尾字
                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");
                }
            }
            log.info("查看关键词库数据：{}" , keywordMap);
        }
    }

}
