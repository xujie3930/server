package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.PurchaseLog;
import com.szmsd.inventory.domain.dto.PurchaseLogAddDTO;
import com.szmsd.inventory.domain.vo.PurchaseLogVO;

import java.util.List;

/**
 * <p>
 * 采购单日志 服务类
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
public interface IPurchaseLogService extends IService<PurchaseLog> {

    /**
     * 查询采购单日志模块列表
     *
     * @param
     * @return 采购单日志模块集合
     */
    List<PurchaseLogVO> selectPurchaseLogList(String id);

    /**
     * 新增采购单日志模块
     *
     * @param purchaseLog 采购单日志模块
     * @return 结果
     */
    int insertPurchaseLog(PurchaseLogAddDTO purchaseLog);


}

