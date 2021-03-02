package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasFormulaMapper;
import com.szmsd.bas.domain.BasFormula;
import com.szmsd.bas.service.IBasFormulaService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 公式表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-07-08
 */
@Service
public class BasFormulaServiceImpl extends ServiceImpl<BasFormulaMapper, BasFormula> implements IBasFormulaService {


    /**
     * 查询公式表模块
     *
     * @param id 公式表模块ID
     * @return 公式表模块
     */
    @Override
    public BasFormula selectBasFormulaById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询公式表模块列表
     *
     * @param BasFormula 公式表模块
     * @return 公式表模块
     */
    @Override
    public List<BasFormula> selectBasFormulaList(BasFormula basFormula) {
        QueryWrapper<BasFormula> where = new QueryWrapper<BasFormula>();
        if (StringUtils.isNotEmpty(basFormula.getCuspriceId())){
            where.eq("cusprice_id",basFormula.getCuspriceId());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增公式表模块
     *
     * @param BasFormula 公式表模块
     * @return 结果
     */
    @Override
    public int insertBasFormula(BasFormula basFormula) {
        return baseMapper.insert(basFormula);
    }

    /**
     * 修改公式表模块
     *
     * @param BasFormula 公式表模块
     * @return 结果
     */
    @Override
    public int updateBasFormula(BasFormula basFormula) {
        return baseMapper.updateById(basFormula);
    }

    /**
     * 批量删除公式表模块
     *
     * @param ids 需要删除的公式表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasFormulaByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除公式表模块信息
     *
     * @param id 公式表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasFormulaById(String id) {
        return baseMapper.deleteById(id);
    }
}
