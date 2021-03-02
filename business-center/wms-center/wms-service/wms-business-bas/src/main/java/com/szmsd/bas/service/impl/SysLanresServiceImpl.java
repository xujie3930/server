package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.SysLanresMapper;
import com.szmsd.bas.domain.SysLanres;
import com.szmsd.bas.service.ISysLanresService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 多语言配置表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-08-06
 */
@Service
public class SysLanresServiceImpl extends ServiceImpl<SysLanresMapper, SysLanres> implements ISysLanresService {


    /**
     * 查询多语言配置表模块
     *
     * @param id 多语言配置表模块ID
     * @return 多语言配置表模块
     */
    @Override
    public SysLanres selectSysLanresById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询多语言配置表模块列表
     *
     * @param SysLanres 多语言配置表模块
     * @return 多语言配置表模块
     */
    @Override
    public List<SysLanres> selectSysLanresList(SysLanres sysLanres) {
        QueryWrapper<SysLanres> where = new QueryWrapper<SysLanres>();
        if (StringUtils.isNotEmpty(sysLanres.getStrid())) {
            where.like("strid", sysLanres.getStrid());
        }
        if (StringUtils.isNotEmpty(sysLanres.getLan1())) {
            where.like("lan1", sysLanres.getLan1());
        }
        if (StringUtils.isNotEmpty(sysLanres.getLan2())) {
            where.like("lan2", sysLanres.getLan2());
        }
        if (StringUtils.isNotEmpty(sysLanres.getCode())) {
            where.eq("code", sysLanres.getCode());
        }
        if (StringUtils.isNotNull(sysLanres.getGrouptype())) {
            where.eq("GROUPTYPE", sysLanres.getGrouptype());
        }
        if (StringUtils.isNotEmpty(sysLanres.getApp())){
            where.eq("app",sysLanres.getApp());
        }
        return baseMapper.selectList(where);
    }

    @Override
    public List<SysLanres> selectSysLanres(SysLanres sysLanres) {
        QueryWrapper<SysLanres> where = new QueryWrapper<SysLanres>();
        if (StringUtils.isNotEmpty(sysLanres.getStrid())) {
            where.eq("strid", sysLanres.getStrid());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增多语言配置表模块
     *
     * @param SysLanres 多语言配置表模块
     * @return 结果
     */
    @Override
    public int insertSysLanres(SysLanres sysLanres) {
        return baseMapper.insert(sysLanres);
    }

    /**
     * 修改多语言配置表模块
     *
     * @param SysLanres 多语言配置表模块
     * @return 结果
     */
    @Override
    public int updateSysLanres(SysLanres sysLanres) {
        return baseMapper.updateById(sysLanres);
    }

    /**
     * 批量删除多语言配置表模块
     *
     * @param ids 需要删除的多语言配置表模块ID
     * @return 结果
     */
    @Override
    public int deleteSysLanresByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除多语言配置表模块信息
     *
     * @param id 多语言配置表模块ID
     * @return 结果
     */
    @Override
    public int deleteSysLanresById(String id) {
        return baseMapper.deleteById(id);
    }


}
