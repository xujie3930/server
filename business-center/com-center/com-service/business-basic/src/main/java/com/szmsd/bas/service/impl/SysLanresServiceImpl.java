package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.SysLanresMapper;
import com.szmsd.bas.domain.SysLanres;
import com.szmsd.bas.service.ISysLanresService;
import com.szmsd.common.core.language.LanguageService;
import com.szmsd.common.core.language.util.LanguageUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.redis.service.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 多语言配置表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-08-06
 */
@Service
public class SysLanresServiceImpl extends ServiceImpl<SysLanresMapper, SysLanres> implements ISysLanresService, LanguageService {

    @Resource
    private RedisService redisService;

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
        if (StringUtils.isNotEmpty(sysLanres.getApp())) {
            where.eq("app", sysLanres.getApp());
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

    @Override
    public void refresh() {
        LambdaQueryWrapper<SysLanres> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(SysLanres::getStrid, SysLanres::getLan1, SysLanres::getLan2);
        queryWrapper.eq(SysLanres::getDeletedStatus, "1");
        List<SysLanres> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            // 更新所有的国际化到缓存中
            for (SysLanres sysLanres : list) {
                updateRedis(sysLanres);
            }
        }
    }

    private void updateRedis(SysLanres sysLanres) {
        Map<String, String> map = new HashMap<>();
        map.put("zh", sysLanres.getStrid());
        map.put("en", sysLanres.getLan1());
        map.put("ar", sysLanres.getLan2());
        this.redisService.setCacheMap(LanguageUtil.buildKey(sysLanres.getStrid()), map);
    }

    @Override
    public void refresh(String key) {
        SysLanres sysLanres = this.selectSysLanresById(key);
        if (null != sysLanres) {
            updateRedis(sysLanres);
        }

    }

    @Override
    public void refresh(List<String> keys) {
        List<SysLanres> list = this.listByIds(keys);
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysLanres sysLanres : list) {
                updateRedis(sysLanres);
            }
        }
    }

    @Override
    public void delete(String key) {
        SysLanres sysLanres = this.selectSysLanresById(key);
        if (null != sysLanres) {
            this.redisService.deleteObject(LanguageUtil.buildKey(sysLanres.getStrid()));
        }
    }

    @Override
    public void deletes(List<String> keys) {
        List<SysLanres> list = this.listByIds(keys);
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysLanres sysLanres : list) {
                this.redisService.deleteObject(LanguageUtil.buildKey(sysLanres.getStrid()));
            }
        }
    }
}
