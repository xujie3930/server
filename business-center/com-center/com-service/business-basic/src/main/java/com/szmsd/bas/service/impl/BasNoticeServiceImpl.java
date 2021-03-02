package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasNoticeMapper;
import com.szmsd.bas.domain.BasNotice;
import com.szmsd.bas.service.IBasNoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 公告通知明细 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */
@Service
public class BasNoticeServiceImpl extends ServiceImpl<BasNoticeMapper, BasNotice> implements IBasNoticeService {


    /**
     * 查询公告通知明细模块
     *
     * @param id 公告通知明细模块ID
     * @return 公告通知明细模块
     */
    @Override
    public BasNotice selectBasNoticeById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询公告通知明细模块列表
     *
     * @param basNotice 公告通知明细模块
     * @return 公告通知明细模块
     */
    @Override
    public List<BasNotice> selectBasNoticeList(BasNotice basNotice) {
        QueryWrapper<BasNotice> where = new QueryWrapper<BasNotice>();
        if (StringUtils.isNotEmpty(basNotice.getStrDate())&& StringUtils.isNotEmpty(basNotice.getEndDate() )) {
            where.between("create_time", basNotice.getStrDate(), basNotice.getEndDate());
        }
        if (StringUtils.isNotEmpty(basNotice.getId())){
            where.eq("id",basNotice.getId());
        }
        if (StringUtils.isNotEmpty(basNotice.getCreateSiteCode())){
            where.eq("create_site_code",basNotice.getCreateSiteCode());
        }
        if (StringUtils.isNotEmpty(basNotice.getCreateSiteName())){
            where.like("create_site_name",basNotice.getCreateSiteName());
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增公告通知明细模块
     *
     * @param basNotice 公告通知明细模块
     * @return 结果
     */
    @Override
    public int insertBasNotice(BasNotice basNotice) {
        return baseMapper.insert(basNotice);
    }

    /**
     * 修改公告通知明细模块
     *
     * @param basNotice 公告通知明细模块
     * @return 结果
     */
    @Override
    public int updateBasNotice(BasNotice basNotice) {
        return baseMapper.updateById(basNotice);
    }

    /**
     * 批量删除公告通知明细模块
     *
     * @param ids 需要删除的公告通知明细模块ID
     * @return 结果
     */
    @Override
    public int deleteBasNoticeByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除公告通知明细模块信息
     *
     * @param id 公告通知明细模块ID
     * @return 结果
     */
    @Override
    public int deleteBasNoticeById(String id) {
        return baseMapper.deleteById(id);
    }
}
