package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasE3MesMapper;
import com.szmsd.bas.domain.BasE3Mes;
import com.szmsd.bas.service.IBasE3MesService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * E3消息表 服务实现类
 * </p>
 *
 * @author admin
 * @since 2020-11-28
 */
@Service
public class BasE3MesServiceImpl extends ServiceImpl<BasE3MesMapper, BasE3Mes> implements IBasE3MesService {

    /**
     * 查询E3消息表模块
     *
     * @param id E3消息表模块ID
     * @return E3消息表模块
     */
    @Override
    public BasE3Mes selectBasE3MesById(String id)
    {
        return baseMapper.selectById(id);
    }

    /**
     * 查询E3消息表模块列表
     *
     * @param basE3Mes E3消息表模块
     * @return E3消息表模块
     */
    @Override
    public List<BasE3Mes> selectBasE3MesList(BasE3Mes basE3Mes) {
        QueryWrapper<BasE3Mes> where = new QueryWrapper<BasE3Mes>();
        where.eq("del_flag","0");
        //当前登录员工编号
        where.eq("emp_code",basE3Mes.getEmpCode());
        Date now = new Date();
        Date yes = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(now);
        String yesterday = simpleDateFormat.format(yes);//获取昨天日期
        //时间范围是两天内
        where.ge("create_time",yesterday+" 00:00:00");
        where.le("create_time",today+" 23:59:59");
        if(StringUtils.isNotEmpty(basE3Mes.getParentTypeCode())){
            where.eq("parent_type_code",basE3Mes.getParentTypeCode());
        }else{
            where.eq("parent_type_code","1");
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增E3消息表模块
     *
     * @param basE3Mes E3消息表模块
     * @return 结果
     */
    @Transactional
    @Override
    public int insertBasE3Mes(BasE3Mes basE3Mes)
    {
        return baseMapper.insert(basE3Mes);
    }

    /**
     * 修改E3消息表模块
     *
     * @param basE3Mes E3消息表模块
     * @return 结果
     */
    @Transactional
    @Override
    public int updateBasE3Mes(BasE3Mes basE3Mes)
    {
        return baseMapper.updateById(basE3Mes);
    }

    /**
     * 批量删除E3消息表模块
     *
     * @param idList 需要删除的E3消息表模块ID
     * @return 结果
     */
    @Transactional
    @Override
    public int batchDel(List<String>  idList) {
        return baseMapper.batchDel(idList);
    }

    @Transactional
    @Override
    public int deleteBySourceId(String sourceId) {
        return baseMapper.deleteBySourceId(sourceId);
    }

}

