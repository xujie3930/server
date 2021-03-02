package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasAppMesMapper;
import com.szmsd.bas.domain.BasAppMes;
import com.szmsd.bas.service.IBasAppMesService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * App消息表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */
@Service
public class BasAppMesServiceImpl extends ServiceImpl<BasAppMesMapper, BasAppMes> implements IBasAppMesService {


    /**
     * 查询App消息表模块
     *
     * @param id App消息表模块ID
     * @return App消息表模块
     */
    @Override
    public BasAppMes selectBasAppMesById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询App消息表模块列表
     *
     * @param basAppMes App消息表模块
     * @return App消息表模块
     */
    @Override
    public List<BasAppMes> selectBasAppMesList(BasAppMes basAppMes) {
        QueryWrapper<BasAppMes> where = new QueryWrapper<BasAppMes>();
        where.eq("del_flag","0");
        Date now = new Date();
        Date yes = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(now);
        String yesterday = simpleDateFormat.format(yes);//获取昨天日期
        //时间范围是两天内
        where.ge("create_time",yesterday+" 00:00:00");
        where.le("create_time",today+" 23:59:59");
        if (StringUtils.isNotEmpty(basAppMes.getParentTypeCode())){
            where.eq("parent_type_code",basAppMes.getParentTypeCode());
        }
        if (StringUtils.isNotEmpty(basAppMes.getParentTypeName())){
            where.eq("parent_type_name",basAppMes.getParentTypeName());
        }
        if (StringUtils.isNotEmpty(basAppMes.getEmpCode())){
            where.eq("emp_code",basAppMes.getEmpCode());
        }
        if (StringUtils.isNotEmpty(basAppMes.getEmpName())){
            where.eq("emp_name",basAppMes.getEmpName());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增App消息表模块
     *
     * @param basAppMes App消息表模块
     * @return 结果
     */
    @Override
    public int insertBasAppMes(BasAppMes basAppMes) {
        return baseMapper.insert(basAppMes);
    }

    /**
     * 修改App消息表模块
     *
     * @param basAppMes App消息表模块
     * @return 结果
     */
    @Override
    public int updateBasAppMes(BasAppMes basAppMes) {
        return baseMapper.updateById(basAppMes);
    }

    /**
     * 批量删除App消息表模块
     *
     * @param ids 需要删除的App消息表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasAppMesByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除App消息表模块信息
     *
     * @param id App消息表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasAppMesById(String id) {
        return baseMapper.deleteById(id);
    }

    @Transactional
    @Override
    public int deleteBySourceId(String sourceId) {
        return baseMapper.deleteBySourceId(sourceId);
    }

    @Override
    public List<BasAppMes> getPushAppMsgList() {
        //查询 重复推送次数小于6，并且推送标识为未推送的
        QueryWrapper<BasAppMes> where = new QueryWrapper<BasAppMes>();
        where.le("repeat_times", 5);
        where.eq("push_flag","0");
        return baseMapper.selectList(where);
    }

    @Override
    public boolean updateBasAppMesList(List<BasAppMes> basAppMesList) {
        return  this.updateBatchById(basAppMesList);
    }
}
