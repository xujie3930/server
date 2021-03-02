package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasTrainNumberMapper;
import com.szmsd.bas.domain.BasTrainNumber;
import com.szmsd.bas.service.IBasTrainNumberService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 车次表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-11-18
 */
@Service
public class BasTrainNumberServiceImpl extends ServiceImpl<BasTrainNumberMapper, BasTrainNumber> implements IBasTrainNumberService {


    /**
     * 查询车次表模块
     *
     * @param id 车次表模块ID
     * @return 车次表模块
     */
    @Override
    public BasTrainNumber selectBasTrainNumberById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询车次表模块列表
     *
     * @param BasTrainNumber 车次表模块
     * @return 车次表模块
     */
    @Override
    public List<BasTrainNumber> selectBasTrainNumberList(BasTrainNumber basTrainNumber) {
        QueryWrapper<BasTrainNumber> where = new QueryWrapper<BasTrainNumber>();
        if (StringUtils.isNotNull(basTrainNumber.getCreateStartTime())&& StringUtils.isNotNull(basTrainNumber.getCreateEndTime() )) {
            where.between("create_time", basTrainNumber.getCreateStartTime(), basTrainNumber.getCreateEndTime());
        }
        if (StringUtils.isNotEmpty(basTrainNumber.getLicensePlate())){
            where.eq("license_plate",basTrainNumber.getLicensePlate());
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增车次表模块
     *
     * @param BasTrainNumber 车次表模块
     * @return 结果
     */
    @Override
    public int insertBasTrainNumber(BasTrainNumber basTrainNumber) {
        return baseMapper.insert(basTrainNumber);
    }

    /**
     * 修改车次表模块
     *
     * @param BasTrainNumber 车次表模块
     * @return 结果
     */
    @Override
    public int updateBasTrainNumber(BasTrainNumber basTrainNumber) {
        return baseMapper.updateById(basTrainNumber);
    }

    /**
     * 批量删除车次表模块
     *
     * @param ids 需要删除的车次表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasTrainNumberByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除车次表模块信息
     *
     * @param id 车次表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasTrainNumberById(String id) {
        return baseMapper.deleteById(id);
    }
}
