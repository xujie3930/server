package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasMesMapper;
import com.szmsd.bas.domain.BasMes;
import com.szmsd.bas.domain.Mes;
import com.szmsd.bas.service.IBasMesService;
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
 * @since 2020-08-20
 */
@Service
public class BasMesServiceImpl extends ServiceImpl<BasMesMapper, BasMes> implements IBasMesService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasMes selectBasMesById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasMes 模块
     * @return 模块
     */
    @Override
    public List<BasMes> selectBasMesList(BasMes basMes) {
        String idList=basMes.getId();
        QueryWrapper<BasMes> where = new QueryWrapper<BasMes>();
        if (StringUtils.isNotEmpty(basMes.getStartTime())&& StringUtils.isNotEmpty(basMes.getEndTime() )) {
            where.between("create_time", basMes.getStartTime(), basMes.getEndTime());
        }
        if (StringUtils.isNotEmpty(basMes.getCreateSite())){
            where.eq("create_site",basMes.getCreateSite());
        }
        if (StringUtils.isNotEmpty(basMes.getCreateSiteCode())){
            where.eq("create_site_code",basMes.getCreateSiteCode());
        }
        if (StringUtils.isNotEmpty(basMes.getCreateBy())){
            where.eq("create_by",basMes.getCreateBy());
        }
        if (StringUtils.isNotEmpty(basMes.getIphone())){
            where.eq("iphone",basMes.getIphone());
        }
        if (StringUtils.isNotEmpty(basMes.getSendIden())){
            where.eq("send_iden",basMes.getSendIden());
        }
        if (StringUtils.isNotEmpty(basMes.getPaySite())){
            where.like("pay_site",basMes.getPaySite());
        }
        if (StringUtils.isNotEmpty(basMes.getPaySiteCode())){
            where.like("pay_site_code",basMes.getPaySiteCode());
        }
        if (!StringUtils.isEmpty(basMes.getId())){
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

        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasMes 模块
     * @return 结果
     */
    @Override
    public int insertBasMes(BasMes basMes) {
        return baseMapper.insert(basMes);
    }

    /**
     * 修改模块
     *
     * @param BasMes 模块
     * @return 结果
     */
    @Override
    public int updateBasMes(BasMes basMes) {
        return baseMapper.updateById(basMes);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasMesByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasMesById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public List<Mes> list(Mes basMes) {
        return baseMapper.list(basMes);
    }
}
