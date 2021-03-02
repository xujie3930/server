package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasTemplateMapper;
import com.szmsd.bas.domain.BasTemplate;
import com.szmsd.bas.service.IBasTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-08-24
 */
@Service
public class BasTemplateServiceImpl extends ServiceImpl<BasTemplateMapper, BasTemplate> implements IBasTemplateService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasTemplate selectBasTemplateById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasTemplate 模块
     * @return 模块
     */
    @Override
    public List<BasTemplate> selectBasTemplateList(BasTemplate basTemplate) {
        QueryWrapper<BasTemplate> where = new QueryWrapper<BasTemplate>();
        if (StringUtils.isNotEmpty(basTemplate.getTemplateType())){
            where.like("template_type",basTemplate.getTemplateType());
        }
        if (StringUtils.isNotEmpty(basTemplate.getTemplateTypeCode())){
            where.eq("template_type_code",basTemplate.getTemplateTypeCode());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasTemplate 模块
     * @return 结果
     */
    @Override
    public int insertBasTemplate(BasTemplate basTemplate) {
        return baseMapper.insert(basTemplate);
    }

    /**
     * 修改模块
     *
     * @param BasTemplate 模块
     * @return 结果
     */
    @Override
    public int updateBasTemplate(BasTemplate basTemplate) {
        return baseMapper.updateById(basTemplate);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasTemplateByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasTemplateById(String id) {
        return baseMapper.deleteById(id);
    }
}
