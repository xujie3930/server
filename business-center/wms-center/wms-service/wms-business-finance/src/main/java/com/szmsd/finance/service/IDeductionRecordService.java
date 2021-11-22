package com.szmsd.finance.service;

import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.FssDeductionRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.finance.enums.CreditConstant;
import com.szmsd.finance.vo.CreditUseInfo;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
     * 更新正常结束的账单信息状态
     */
    void updateDeductionRecordStatus(List<String> updateCusCodeList);

    /**
     * 按周期更新正常的账单
     */
    void updateRecordStatusByCreditTimeInterval();

    Map<String, CreditUseInfo> queryTimeCreditUse(String cusCode,List<String> currencyCodeList,List<CreditConstant.CreditBillStatusEnum> statusList);

    /**
     * 更新授信额度的账单（还款销账）
     * @param addMoney 销账额
     * @param cusCode 客户code
     * @param currencyCode 币别
     */
    void addForCreditBill(BigDecimal addMoney,String cusCode,String currencyCode);
}

