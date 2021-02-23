package com.szmsd.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.system.domain.SysLanres;
import com.szmsd.system.mapper.SysLanresMapper;
import com.szmsd.system.service.ISysLanresService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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

    @Override
    public List<SysLanres> selectLanres(SysLanres sysLanres) {
        QueryWrapper<SysLanres> where = new QueryWrapper<SysLanres>();
//        if (StringUtils.isNotEmpty(sysLanres.getStrid())){
//            where.eq("strid",sysLanres.getStrid());
//        }
        where.isNull("lan1");
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
        if (StringUtils.isEmpty(sysLanres.getLan1())) {
            sysLanres.setLan1(null);
        }
        if (StringUtils.isEmpty(sysLanres.getLan2())) {
            sysLanres.setLan2(null);
        }
        QueryWrapper<SysLanres> wrapper = new QueryWrapper<>();
        wrapper.eq("id", sysLanres.getId());
        return baseMapper.update(sysLanres, wrapper);
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
    public R importExcel(MultipartFile file) throws Exception {
        ExcelUtil<SysLanres> util = new ExcelUtil<>(SysLanres.class);
        List<SysLanres> list = util.importExcel(file.getInputStream());
        for (SysLanres sysLanres : list) {
            if (sysLanres.getStrid() == null) {
                continue;
            }
            SysLanres newSysLanres = new SysLanres();
            newSysLanres.setStrid(sysLanres.getStrid());
            List<SysLanres> selectSysLanres = this.selectSysLanres(newSysLanres);
            if (selectSysLanres.size() != 0) {
                sysLanres.setId(selectSysLanres.get(0).getId());
                int i = this.updateSysLanres(sysLanres);
                if (i != 1) {
                    continue;
                }
            } else {
                String uid = UUID.randomUUID().toString().substring(0, 8);
                sysLanres.setCode(uid);
                int insert = baseMapper.insert(sysLanres);
                if (insert != 1) {
                    continue;
                }
            }

        }
        return R.ok();
    }


}
