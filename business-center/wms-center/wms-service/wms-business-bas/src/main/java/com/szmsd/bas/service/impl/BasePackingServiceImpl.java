package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.mapper.BasePackingMapper;
import com.szmsd.bas.service.IBasePackingService;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.http.api.feign.HtpBasFeignService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author l
 * @since 2021-03-06
 */
@Service
public class BasePackingServiceImpl extends ServiceImpl<BasePackingMapper, BasePacking> implements IBasePackingService {

    @Resource
    private HtpBasFeignService htpBasFeignService;

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasePacking selectBasePackingById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param basePacking 模块
     * @return 模块
     */
    @Override
    public List<BasePackingDto> selectBasePackingList(BasePacking basePacking) {
        return baseMapper.selectBasePackingGroup(basePacking.getPackingMaterialType());
    }

    @Override
    public List<BasePacking> selectBasePackingPage(BasePackingQueryDto basePackingQueryDto) {
        QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<BasePacking>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "package_material_code", basePackingQueryDto.getPackageMaterialCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "package_material_name", basePackingQueryDto.getPackageMaterialName());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "warehouse_code", basePackingQueryDto.getWarehouseCode());
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }

    @Override
    public List<BasePackingDto> selectBasePacking() {
        return baseMapper.selectBasePackingGroup(null);
    }

    /**
     * 新增模块
     *
     * @param basePacking 模块
     * @return 结果
     */
    @Override
    public int insertBasePacking(BasePacking basePacking) {
        return baseMapper.insert(basePacking);
    }

    /**
     * 修改模块
     *
     * @param basePacking 模块
     * @return 结果
     */
    @Override
    public int updateBasePacking(BasePacking basePacking) throws IllegalAccessException {
        if(StringUtils.isNotEmpty(basePacking.getCurrency())) {
            BasePacking basePacking1 = super.getById(basePacking.getId());
            QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("warehouse_code", basePacking1.getWarehouseCode());
            queryWrapper.eq("packing_material_type", basePacking1.getPackingMaterialType());
            queryWrapper.isNotNull("currency");
            queryWrapper.select("count(distinct currency) as count,currency");
            queryWrapper.groupBy("currency");
            Map<String, Object> map = super.getMap(queryWrapper);
            if(map==null){
                return baseMapper.updateById(basePacking);
            }
            if (map.containsKey("count")) {
                if (map.get("count") == null) {
                    return baseMapper.updateById(basePacking);
                } else {
                    if ((Long) map.get("count") != 0L) {
                        if (basePacking.getCurrency().equals(map.get("currency"))) {
                            return baseMapper.updateById(basePacking);
                        } else {
                            throw new BaseException("该仓库该物料货币唯一，请勿添加其他货币");
                        }
                    }
                }
            }
        }
        return baseMapper.updateById(basePacking);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasePackingByIds(List<Long> ids) {
        QueryWrapper queryWrapper = new QueryWrapper();
        for (Long l : ids) {

        }

        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasePackingById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public List<BasePacking> queryPackingList(BaseProductConditionQueryDto conditionQueryDto) {
        if (CollectionUtils.isEmpty(conditionQueryDto.getSkus())) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BasePacking> queryWrapper = Wrappers.lambdaQuery();
        if (null != conditionQueryDto.getWarehouseCode()) {
            queryWrapper.eq(BasePacking::getWarehouseCode, conditionQueryDto.getWarehouseCode());
        }
        queryWrapper.in(BasePacking::getPackageMaterialCode, conditionQueryDto.getSkus());
        List<BasePacking> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public BasePacking queryByCode(BasePackingConditionQueryDto conditionQueryDto) {
        LambdaQueryWrapper<BasePacking> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BasePacking::getPackageMaterialCode, conditionQueryDto.getCode());
        List<BasePacking> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public void createPackings(CreatePackingRequest createPackingRequest){
        QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("package_material_code", createPackingRequest.getPackageMaterialCode());
        //处理时间
        String operationOn = createPackingRequest.getOperateOn();
        createPackingRequest.setOperateOn(null);
        BasePacking basePacking = BeanMapperUtil.map(createPackingRequest,BasePacking.class);
        if(StringUtils.isNotEmpty(operationOn)){
            Date  d = dealUTZTime(operationOn);
            basePacking.setOperateOn(d);
        }
        if(super.count(queryWrapper)==1){
            UpdateWrapper<BasePacking> basePackingUpdateWrapper = new UpdateWrapper<>();
            basePackingUpdateWrapper.eq("package_material_code",createPackingRequest.getPackageMaterialCode());
            super.update(basePacking,basePackingUpdateWrapper);
        }else{
            super.save(basePacking);
        }
    }

    private Date dealUTZTime(String time){
        Date date = new Date();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

