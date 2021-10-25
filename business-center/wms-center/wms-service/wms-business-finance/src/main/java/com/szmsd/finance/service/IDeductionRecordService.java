package com.szmsd.finance.service;

import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.FssDeductionRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 扣费信息记录表 服务类
 * </p>
 *
 * @author 11
 * @since 2021-10-14
 */
public interface IDeductionRecordService extends IService<FssDeductionRecord> {

    /**
     * 查询扣费信息记录表模块
     *
     * @param id 扣费信息记录表模块ID
     * @return 扣费信息记录表模块
     */
    FssDeductionRecord selectDeductionRecordById(String id);

    /**
     * 查询扣费信息记录表模块列表
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 扣费信息记录表模块集合
     */
    List<FssDeductionRecord> selectDeductionRecordList(FssDeductionRecord fssDeductionRecord);

    /**
     * 新增扣费信息记录表模块
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 结果
     */
    int insertDeductionRecord(FssDeductionRecord fssDeductionRecord);

    /**
     * 修改扣费信息记录表模块
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 结果
     */
    int updateDeductionRecord(FssDeductionRecord fssDeductionRecord);

    /**
     * 批量删除扣费信息记录表模块
     *
     * @param ids 需要删除的扣费信息记录表模块ID
     * @return 结果
     */
    int deleteDeductionRecordByIds(List<String> ids);

    /**
     * 删除扣费信息记录表模块信息
     *
     * @param id 扣费信息记录表模块ID
     * @return 结果
     */
    int deleteDeductionRecordById(String id);

    /**
     * 更新账单信息
     */
    void updateDeductionRecordStatus(List<String> updateCusCodeList);

    /**
     * 按周期更新正常的账单
     */
    void updateRecordStatusByCreditTimeInterval();
}

