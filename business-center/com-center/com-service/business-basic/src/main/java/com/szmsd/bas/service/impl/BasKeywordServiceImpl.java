package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasKeywordMapper;
import com.szmsd.bas.domain.BasKeyword;
import com.szmsd.bas.service.IBasKeywordService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-06-13
 */
@Service
public class BasKeywordServiceImpl extends ServiceImpl<BasKeywordMapper, BasKeyword> implements IBasKeywordService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasKeyword selectBasKeywordById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasKeyword 模块
     * @return 模块
     */
    @Override
    public List<BasKeyword> selectBasKeywordList(BasKeyword basKeyword) {
        String idList=basKeyword.getId();
        QueryWrapper<BasKeyword> where = new QueryWrapper<BasKeyword>();
        if (!StringUtils.isEmpty(basKeyword.getId())){
            List<String> strings=new ArrayList<>();
            if(idList.contains(","))
            {
                String[] split = idList.split(",");
                strings = Arrays.asList(split);
            }else{
                strings.add(idList);
            }

            where.in("id",strings);
        }
        if (StringUtils.isNotEmpty(basKeyword.getKeywordCode())){
            where.eq("keyword_code",basKeyword.getKeywordCode());
        }
        if (StringUtils.isNotEmpty(basKeyword.getKeywordName())){
            where.eq("keyword_name",basKeyword.getKeywordName());
        }
        if (com.szmsd.common.core.utils.StringUtils.isNotEmpty(basKeyword.getCityName())){
            where.eq("city_name",basKeyword.getCityName());
        }
        if (com.szmsd.common.core.utils.StringUtils.isNotEmpty(basKeyword.getCityCode())){
            where.eq("city_code",basKeyword.getCityCode());
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasKeyword 模块
     * @return 结果
     */
    @Override
    public int insertBasKeyword(BasKeyword basKeyword) {
         return    baseMapper.insert(basKeyword);

    }

    /**
     * 修改模块
     *
     * @param BasKeyword 模块
     * @return 结果
     */
    @Override
    public int updateBasKeyword(BasKeyword basKeyword) {
        return baseMapper.updateById(basKeyword);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasKeywordByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasKeywordById(String id) {
        return baseMapper.deleteById(id);
    }

    /**
     * 根据目的地删除关键字
     * @param siteCode
     * @return
     */
    @Override
    public int deleteBydestination(String siteCode) {
        return baseMapper.deleteBydestination(siteCode);
    }
}
