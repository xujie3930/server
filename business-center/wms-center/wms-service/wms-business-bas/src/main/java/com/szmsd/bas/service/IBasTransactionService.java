package com.szmsd.bas.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasTransaction;
import com.szmsd.bas.dto.BasTransactionDTO;

/**
 * <p>
 * transaction - 接口版本表 - 用来做幂等校验 服务类
 * </p>
 *
 * @author liangchao
 * @since 2021-03-06
 */
public interface IBasTransactionService extends IService<BasTransaction> {

    void save(BasTransactionDTO basTransactionDTO);

    Boolean idempotent(String apiCode, String transactionId);
}