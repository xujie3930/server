package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.constant.TransactionConstant;
import com.szmsd.bas.domain.BasTransaction;
import com.szmsd.bas.dto.BasTransactionDTO;
import com.szmsd.bas.mapper.BasTransactionMapper;
import com.szmsd.bas.service.IBasTransactionService;
import com.szmsd.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * transaction - 接口版本表 - 用来做幂等校验 服务实现类
 * </p>
 *
 * @author liangchao
 * @since 2021-03-06
 */
@Service
@Slf4j
public class BasTransactionServiceImpl extends ServiceImpl<BasTransactionMapper, BasTransaction> implements IBasTransactionService {

    @Resource
    private RedisService redisService;

    @Override
    public void save(BasTransactionDTO basTransactionDTO) {
        String apiCode = basTransactionDTO.getApiCode();
        String transactionId = basTransactionDTO.getTransactionId();
        this.save(new BasTransaction().setApiCode(apiCode).setTransactionId(transactionId));
        // 保存redis
        saveRedis(apiCode, transactionId);
    }

    /**
     * 是否幂等 true 幂等 反之false
     * @param apiCode
     * @param transactionId
     * @return
     */
    @Override
    public Boolean idempotent(String apiCode, String transactionId) {
        // 判断缓存是否存在
        boolean idempotent = redisContain(apiCode, transactionId);
        if (!idempotent) {
            BasTransaction basTransaction = baseMapper.selectOne(new QueryWrapper<BasTransaction>().eq("api_code", apiCode).eq("transaction_id", transactionId));
            if (basTransaction != null) {
                // 保存redis
                saveRedis(apiCode, transactionId);
                idempotent = true;
            }
        }
        log.info("接口幂等判断：apiCode={}, transactionId={}, idempotent={}", apiCode, transactionId, idempotent);
        return idempotent;
    }

    private String getRedisKey(String apiCode) {
        String redisKey = String.format(TransactionConstant.transactionApiCode, apiCode);
        return redisKey;
    }

    private boolean redisContain(String key, String value) {
        String redisKey = getRedisKey(key);
        List<String> cacheList = redisService.getCacheList(redisKey);
        return cacheList.contains(value);
    }

    private void saveRedis(String apiCode, String transactionId) {
        try {
            String redisKey = getRedisKey(apiCode);
            redisService.setCacheList(redisKey, Arrays.asList(transactionId));
        } catch (Exception e) {
            log.error("redis保存异常[transaction:{} - {}]", apiCode, transactionId);
        }
    }

}

