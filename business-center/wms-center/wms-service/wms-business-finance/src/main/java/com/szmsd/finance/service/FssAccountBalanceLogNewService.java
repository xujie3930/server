package com.szmsd.finance.service;

import com.szmsd.finance.entity.FssAccountBalanceLogNewEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 账户余额表日志新表 服务类
 * </p>
 *
 * @author xujie
 * @since 2023-02-10
 */
public interface FssAccountBalanceLogNewService extends IService<FssAccountBalanceLogNewEntity> {

    void autoSyncBalance();
}
