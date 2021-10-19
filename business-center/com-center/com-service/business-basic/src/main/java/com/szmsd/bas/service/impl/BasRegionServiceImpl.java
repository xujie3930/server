package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasRegionQueryDTO;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.dao.BasRegionMapper;
import com.szmsd.bas.service.IBasRegionService;

import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 地区信息 服务实现类
 * </p>
 *
 * @author gen
 * @since 2020-11-13
 */
@Service
public class BasRegionServiceImpl extends ServiceImpl<BasRegionMapper, BasRegion> implements IBasRegionService {

    /**
     * 查询地区信息模块
     *
     * @param id 地区信息模块ID
     * @return 地区信息模块
     */
    @Override
    public BasRegion selectBasRegionById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询地区信息模块列表
     *
     * @param basRegion 地区信息模块
     * @return 地区信息模块
     */
    @Override
    public List<BasRegion> selectBasRegionList(BasRegionQueryDTO basRegion) {
        QueryWrapper<BasRegion> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "name", basRegion.getName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "address_code", basRegion.getAddressCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "type", basRegion.getType());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "p_id", basRegion.getPId());
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 查询国家
     *
     * @param
     */
    @Override
    public List<BasRegion> listCountry() {
        BasRegion basRegion = new BasRegion();
        basRegion.setType(1);
        QueryWrapper<BasRegion> where = new QueryWrapper<BasRegion>();
        where.setEntity(basRegion);
        where.orderByDesc("name");
        return baseMapper.selectList(where);
    }

    /**
     * 新增地区信息模块
     *
     * @param basRegion 地区信息模块
     * @return 结果
     */
    @Override
    public int insertBasRegion(BasRegion basRegion) {
        return baseMapper.insert(basRegion);
    }

    /**
     * 修改地区信息模块
     *
     * @param basRegion 地区信息模块
     * @return 结果
     */
    @Override
    public int updateBasRegion(BasRegion basRegion) {
        return baseMapper.updateById(basRegion);
    }

    /**
     * 批量删除地区信息模块
     *
     * @param ids 需要删除的地区信息模块ID
     * @return 结果
     */
    @Override
    public int deleteBasRegionByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除地区信息模块信息
     *
     * @param id 地区信息模块ID
     * @return 结果
     */
    @Override
    public int deleteBasRegionById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public List<BasRegionSelectListVO> selectList(BasRegionSelectListQueryDto queryDto) {
        LambdaQueryWrapper<BasRegion> queryWrapper = Wrappers.lambdaQuery();
        // select column
        queryWrapper.select(BasRegion::getId, BasRegion::getName, BasRegion::getAddressCode, BasRegion::getEnName);
        // query condition
        if (Objects.nonNull(queryDto.getPId())) {
            queryWrapper.eq(BasRegion::getPId, queryDto.getPId());
        }
        // 地址类别:1国家 2省份 3市 4区 5街道
        // default type 1
        if (Objects.isNull(queryDto.getType())) {
            queryDto.setType(1);
        }
        queryWrapper.eq(BasRegion::getType, queryDto.getType());
        if (StringUtils.isNotEmpty(queryDto.getName())) {
            queryWrapper.like(BasRegion::getName, queryDto.getName().trim());
        }
        if (StringUtils.isNotEmpty(queryDto.getAddressCode())) {
            queryWrapper.like(BasRegion::getAddressCode, queryDto.getAddressCode().trim());
        }
        if (StringUtils.isNotEmpty(queryDto.getEnName())) {
            queryWrapper.like(BasRegion::getEnName, queryDto.getEnName().trim());
        }
        List<BasRegion> regionList = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(regionList)) {
            return BeanMapperUtil.mapList(regionList, BasRegionSelectListVO.class);
        }
        return Collections.emptyList();
    }

}
