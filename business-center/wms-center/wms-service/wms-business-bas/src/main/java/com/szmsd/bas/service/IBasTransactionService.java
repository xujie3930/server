package com.szmsd.bas.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasTransaction;

/**
 * <p>
 * transaction - 接口版本表 - 用来做幂等校验 服务类
 * </p>
 *
 * @author liangchao
 * @since 2021-03-06
 */
public interface IBasTransactionService extends IService<BasTransaction> {

    Boolean idempotent(String apiCode, String transactionId);

}

