package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasCuspriceCodeMapper;
import com.szmsd.bas.domain.BasCuspriceCode;
import com.szmsd.bas.service.IBasCuspriceCodeService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 客户报价子表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-09-21
 */
@Service
public class BasCuspriceCodeServiceImpl extends ServiceImpl<BasCuspriceCodeMapper, BasCuspriceCode> implements IBasCuspriceCodeService {


    /**
     * 查询客户报价子表模块
     *
     * @param id 客户报价子表模块ID
     * @return 客户报价子表模块
     */
    @Override
    public BasCuspriceCode selectBasCuspriceCodeById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询客户报价子表模块列表
     *
     * @param BasCuspriceCode 客户报价子表模块
     * @return 客户报价子表模块
     */
    @Override
    public List<BasCuspriceCode> selectBasCuspriceCodeList(BasCuspriceCode basCuspriceCode) {
        QueryWrapper<BasCuspriceCode> where = new QueryWrapper<BasCuspriceCode>();
        if (StringUtils.isNotEmpty(basCuspriceCode.getCuspriceId())){
            where.eq("cusprice_id",basCuspriceCode.getCuspriceId());
        }
        if (StringUtils.isNotEmpty(basCuspriceCode.getTypes())){
            where.eq("types",basCuspriceCode.getTypes());
        }
        if (StringUtils.isNotEmpty(basCuspriceCode.getCode())){
            where.eq("code",basCuspriceCode.getCode());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增客户报价子表模块
     *
     * @param BasCuspriceCode 客户报价子表模块
     * @return 结果
     */
    @Override
    public int insertBasCuspriceCode(BasCuspriceCode basCuspriceCode) {
        return baseMapper.insert(basCuspriceCode);
    }

    /**
     * 修改客户报价子表模块
     *
     * @param BasCuspriceCode 客户报价子表模块
     * @return 结果
     */
    @Override
    public int updateBasCuspriceCode(BasCuspriceCode basCuspriceCode) {
        return baseMapper.updateById(basCuspriceCode);
    }

    /**
     * 批量删除客户报价子表模块
     *
     * @param ids 需要删除的客户报价子表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCuspriceCodeByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除客户报价子表模块信息
     *
     * @param id 客户报价子表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCuspriceCodeById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public int deleteByCusId(String cuspriceId) {
        return baseMapper.deleteByCusId(cuspriceId);
    }
}
