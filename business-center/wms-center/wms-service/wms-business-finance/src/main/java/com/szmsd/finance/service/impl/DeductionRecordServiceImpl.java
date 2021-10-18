package com.szmsd.finance.service.impl;

import com.szmsd.finance.domain.FssDeductionRecord;
import com.szmsd.finance.mapper.DeductionRecordMapper;
import com.szmsd.finance.service.IDeductionRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 扣费信息记录表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-10-14
 */
@Service
public class DeductionRecordServiceImpl extends ServiceImpl<DeductionRecordMapper, FssDeductionRecord> implements IDeductionRecordService {

    /**
     * 查询扣费信息记录表模块
     *
     * @param id 扣费信息记录表模块ID
     * @return 扣费信息记录表模块
     */
    @Override
    public FssDeductionRecord selectDeductionRecordById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询扣费信息记录表模块列表
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 扣费信息记录表模块
     */
    @Override
    public List<FssDeductionRecord> selectDeductionRecordList(FssDeductionRecord fssDeductionRecord) {
        QueryWrapper<FssDeductionRecord> where = new QueryWrapper<FssDeductionRecord>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增扣费信息记录表模块
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 结果
     */
    @Override
    public int insertDeductionRecord(FssDeductionRecord fssDeductionRecord) {
        return baseMapper.insert(fssDeductionRecord);
    }

    /**
     * 修改扣费信息记录表模块
     *
     * @param fssDeductionRecord 扣费信息记录表模块
     * @return 结果
     */
    @Override
    public int updateDeductionRecord(FssDeductionRecord fssDeductionRecord) {
        return baseMapper.updateById(fssDeductionRecord);
    }

    /**
     * 批量删除扣费信息记录表模块
     *
     * @param ids 需要删除的扣费信息记录表模块ID
     * @return 结果
     */
    @Override
    public int deleteDeductionRecordByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除扣费信息记录表模块信息
     *
     * @param id 扣费信息记录表模块ID
     * @return 结果
     */
    @Override
    public int deleteDeductionRecordById(String id) {
        return baseMapper.deleteById(id);
    }


}

