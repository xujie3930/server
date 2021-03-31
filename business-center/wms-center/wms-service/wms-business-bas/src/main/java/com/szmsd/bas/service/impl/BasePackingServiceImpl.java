package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingQueryDto;
import com.szmsd.bas.mapper.BasePackingMapper;
import com.szmsd.bas.service.IBasePackingService;
import com.szmsd.bas.util.ObjectUtil;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.PackingRequest;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    public List<BasePacking> selectBasePackingList(BasePacking basePacking) {
        QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<BasePacking>();
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<BasePacking> selectBasePackingPage(BasePackingQueryDto basePackingQueryDto) {
        QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<BasePacking>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "code", basePackingQueryDto.getCode());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "name", basePackingQueryDto.getName());
        queryWrapper.orderByDesc("create_time");
        return super.list(queryWrapper);
    }

    @Override
    public List<BasePacking> selectBasePackingParent() {
        QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<BasePacking>();
        queryWrapper.isNull("p_id");
        queryWrapper.orderByDesc("name");
        return super.list(queryWrapper);
    }

    /**
     * 新增模块
     *
     * @param basePacking 模块
     * @return 结果
     */
    @Override
    public int insertBasePacking(BasePacking basePacking) {
        QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",basePacking.getCode());
        int count  = super.count(queryWrapper);
        if(count!=0){
            throw new BaseException("物料编码重复，请更换之后重新提交");
        }
        PackingRequest packingRequest = BeanMapperUtil.map(basePacking, PackingRequest.class);
        if (basePacking.getPId() != null) {
            R<ResponseVO> r = htpBasFeignService.createPacking(packingRequest);
            if (!r.getData().getSuccess()) {
                throw new BaseException("传wms失败:" + r.getData().getMessage());
            }
        }
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
        QueryWrapper<BasePacking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", basePacking.getId());
        BasePacking packing = super.getOne(queryWrapper);
        if (packing.getPId() != null) {
            PackingRequest packingRequest = BeanMapperUtil.map(basePacking, PackingRequest.class);
            ObjectUtil.fillNull(packingRequest, packing);
            R<ResponseVO> r = htpBasFeignService.createPacking(packingRequest);
            if (!r.getData().getSuccess()) {
                throw new BaseException("传wms失败:" + r.getData().getMessage());
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


}

