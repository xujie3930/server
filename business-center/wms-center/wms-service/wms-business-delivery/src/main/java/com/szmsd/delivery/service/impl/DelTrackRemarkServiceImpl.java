package com.szmsd.delivery.service.impl;

import com.szmsd.delivery.domain.DelTrackRemark;
import com.szmsd.delivery.mapper.DelTrackRemarkMapper;
import com.szmsd.delivery.service.IDelTrackRemarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 轨迹备注表 服务实现类
 * </p>
 *
 * @author YM
 * @since 2022-05-06
 */
@Service
public class DelTrackRemarkServiceImpl extends ServiceImpl<DelTrackRemarkMapper, DelTrackRemark> implements IDelTrackRemarkService {

    @Autowired
    private RedisTemplate redisTemplate;

    public final static String TRACK_REMARK_KEY = "Track:Remark";

    /**
     * 查询轨迹备注表模块
     *
     * @param id 轨迹备注表模块ID
     * @return 轨迹备注表模块
     */
    @Override
    public DelTrackRemark selectDelTrackRemarkById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询轨迹备注表模块列表
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 轨迹备注表模块
     */
    @Override
    public List<DelTrackRemark> selectDelTrackRemarkList(DelTrackRemark delTrackRemark) {
        QueryWrapper<DelTrackRemark> where = new QueryWrapper<DelTrackRemark>();
        where.orderByDesc("id");
        return baseMapper.selectList(where);
    }

    /**
     * 新增轨迹备注表模块
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 结果
     */
    @Override
    public int insertDelTrackRemark(DelTrackRemark delTrackRemark) {
        redisTemplate.opsForHash().put(TRACK_REMARK_KEY, delTrackRemark.getTrackDescription(), delTrackRemark.getTrackRemark());
        return baseMapper.insert(delTrackRemark);
    }

    /**
     * 修改轨迹备注表模块
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 结果
     */
    @Override
    public int updateDelTrackRemark(DelTrackRemark delTrackRemark) {
        redisTemplate.opsForHash().put(TRACK_REMARK_KEY, delTrackRemark.getTrackDescription(), delTrackRemark.getTrackRemark());
        return baseMapper.updateById(delTrackRemark);
    }

    /**
     * 批量删除轨迹备注表模块
     *
     * @param ids 需要删除的轨迹备注表模块ID
     * @return 结果
     */
    @Override
    public int deleteDelTrackRemarkByIds(List<String> ids) {
        List<DelTrackRemark> remarks = this.listByIds(ids);
        if (CollectionUtils.isNotEmpty(remarks)) {
            remarks.forEach(item -> {
                redisTemplate.opsForHash().delete(TRACK_REMARK_KEY, item.getTrackDescription());
            });
        }
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除轨迹备注表模块信息
     *
     * @param id 轨迹备注表模块ID
     * @return 结果
     */
    @Override
    public int deleteDelTrackRemarkById(String id) {
        DelTrackRemark remark = this.getById(id);
        if (remark != null) {
            redisTemplate.opsForHash().delete(TRACK_REMARK_KEY, remark.getTrackDescription());
        }
        return baseMapper.deleteById(id);
    }


}

