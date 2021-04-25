package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.inventory.domain.PurchaseLog;
import com.szmsd.inventory.domain.dto.PurchaseLogAddDTO;
import com.szmsd.inventory.domain.vo.PurchaseLogVO;
import com.szmsd.inventory.mapper.PurchaseLogMapper;
import com.szmsd.inventory.service.IPurchaseLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 采购单日志 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Service
public class PurchaseLogServiceImpl extends ServiceImpl<PurchaseLogMapper, PurchaseLog> implements IPurchaseLogService {

    /**
     * 查询采购单日志模块列表
     *
     * @param
     * @return 采购单日志模块
     */
    @Override
    public List<PurchaseLogVO> selectPurchaseLogList(String id) {
        return baseMapper.selectPurchaseLogList(id);
    }

    /**
     * 新增采购单日志模块
     *
     * @param purchaseLog 采购单日志模块
     * @return 结果
     */
    @Override
    public int insertPurchaseLog(PurchaseLogAddDTO purchaseLog) {
        purchaseLog.formatLogDetails();
        PurchaseLog purchaseLogAdd = new PurchaseLog();
        BeanUtils.copyProperties(purchaseLog, purchaseLogAdd);
        return baseMapper.insert(purchaseLogAdd);
    }


}

