package com.szmsd.bas.service.impl;

import com.szmsd.bas.mapper.InitRedisCacheMapper;
import com.szmsd.bas.service.InitRedisCacheService;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.redis.service.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InitRedisCacheServiceImpl implements InitRedisCacheService {

    @Resource
    private InitRedisCacheMapper initRedisCacheMapper;

    @Resource
    private RedisService redisService;

    /**
     * 更新仓库redis
     */
    @Override
    public void updateWarehouseRedis() {
        List<Map<String, String>> warehouse = initRedisCacheMapper.getWarehouse();
        if (CollectionUtils.isEmpty(warehouse)) {
            return;
        }
        Map<String, Map<String, String>> code = warehouse.stream().collect(Collectors.toMap(e -> e.get("code"), v -> v, (v1, v2) -> v1));
        redisService.deleteObject(RedisLanguageTable.BAS_WAREHOUSE);
        redisService.setCacheMap(RedisLanguageTable.BAS_WAREHOUSE, code);
    }
}
