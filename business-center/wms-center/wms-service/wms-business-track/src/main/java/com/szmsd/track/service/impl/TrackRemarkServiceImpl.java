package com.szmsd.track.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.track.domain.TrackRemark;
import com.szmsd.track.mapper.TrackRemarkMapper;
import com.szmsd.track.service.ITrackRemarkService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 轨迹备注表 服务实现类
 * </p>
 *
 * @author YM
 * @since 2022-05-06
 */
@Service
public class TrackRemarkServiceImpl extends ServiceImpl<TrackRemarkMapper, TrackRemark> implements ITrackRemarkService {

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
    public TrackRemark selectDelTrackRemarkById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询轨迹备注表模块列表
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 轨迹备注表模块
     */
    @Override
    public List<TrackRemark> selectDelTrackRemarkList(TrackRemark delTrackRemark) {
        LambdaQueryWrapper<TrackRemark> where = new LambdaQueryWrapper<TrackRemark>();
        where.like(StringUtils.isNotBlank(delTrackRemark.getTrackDescription()), TrackRemark::getTrackDescription, delTrackRemark.getTrackDescription());
        where.like(StringUtils.isNotBlank(delTrackRemark.getTrackRemark()), TrackRemark::getTrackRemark, delTrackRemark.getTrackRemark());
        where.orderByDesc(TrackRemark::getId);
        return baseMapper.selectList(where);
    }

    /**
     * 新增轨迹备注表模块
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 结果
     */
    @Override
    public int insertDelTrackRemark(TrackRemark delTrackRemark) {
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
    public int updateDelTrackRemark(TrackRemark delTrackRemark) {
        // 更新时需要删除原始数据缓存 再添加新缓存
        TrackRemark trackRemark = baseMapper.selectById(delTrackRemark.getId());
        if (trackRemark != null) {
            redisTemplate.opsForHash().delete(TRACK_REMARK_KEY, trackRemark.getTrackDescription());
        }
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
        List<TrackRemark> remarks = this.listByIds(ids);
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
        TrackRemark remark = this.getById(id);
        if (remark != null) {
            redisTemplate.opsForHash().delete(TRACK_REMARK_KEY, remark.getTrackDescription());
        }
        return baseMapper.deleteById(id);
    }


}

