package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasTransaction;
import com.szmsd.bas.mapper.BasTransactionMapper;
import com.szmsd.bas.service.IBasTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    /**
     * 是否幂等 true 幂等 反之false
     * @param apiCode
     * @param transactionId
     * @return
     */
    @Override
    public Boolean idempotent(String apiCode, String transactionId) {
        boolean idempotent = false;
        BasTransaction basTransaction = baseMapper.selectOne(new QueryWrapper<BasTransaction>().eq("api_code", apiCode).eq("transaction_id", transactionId));
        if (basTransaction != null) {
            idempotent = true;
        }
        log.info("接口幂等判断：apiCode={}, transactionId={}, idempotent={}", apiCode, transactionId, idempotent);
        return idempotent;
    }

}

