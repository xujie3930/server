package com.szmsd.http.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.http.domain.HtpWarehouseMapping;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingDTO;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingQueryDTO;
import com.szmsd.http.mapper.HtpWarehouseMappingMapper;
import com.szmsd.http.service.IHtpWarehouseMappingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.http.vo.mapping.HtpWarehouseMappingVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 仓库与仓库关联映射 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-12-13
 */
@Service
public class HtpWarehouseMappingServiceImpl extends ServiceImpl<HtpWarehouseMappingMapper, HtpWarehouseMapping> implements IHtpWarehouseMappingService {


    /**
     * 查询仓库与仓库关联映射模块
     *
     * @param id 仓库与仓库关联映射模块ID
     * @return 仓库与仓库关联映射模块
     */
    @Override
    public HtpWarehouseMappingVO selectHtpWarehouseMappingById(Integer id) {
        return baseMapper.selectOneById(id);
    }

    /**
     * 查询仓库与仓库关联映射模块列表
     *
     * @param htpWarehouseMapping 仓库与仓库关联映射模块
     * @return 仓库与仓库关联映射模块
     */
    @Override
    public List<HtpWarehouseMappingVO> selectHtpWarehouseMappingList(HtpWarehouseMappingQueryDTO htpWarehouseMapping) {

        String originSystem = htpWarehouseMapping.getOriginSystem();
        String warehouseCode = htpWarehouseMapping.getWarehouseCode();
        String warehouseName = htpWarehouseMapping.getWarehouseName();

        String mappingSystem = htpWarehouseMapping.getMappingSystem();
        String mappingWarehouseCode = htpWarehouseMapping.getMappingWarehouseCode();
        String mappingWarehouseName = htpWarehouseMapping.getMappingWarehouseName();

        Integer status = htpWarehouseMapping.getStatus();

        return baseMapper.selectHtpWarehouseMappingList(Wrappers.<HtpWarehouseMapping>lambdaQuery()
                .eq(Objects.nonNull(status), HtpWarehouseMapping::getStatus, status)

                .eq(StringUtils.isNotBlank(originSystem), HtpWarehouseMapping::getOriginSystem, originSystem)
                .eq(StringUtils.isNotBlank(warehouseCode), HtpWarehouseMapping::getWarehouseCode, warehouseCode)
                .eq(StringUtils.isNotBlank(warehouseName), HtpWarehouseMapping::getWarehouseName, warehouseName)

                .eq(StringUtils.isNotBlank(mappingSystem), HtpWarehouseMapping::getMappingSystem, mappingSystem)
                .eq(StringUtils.isNotBlank(mappingWarehouseCode), HtpWarehouseMapping::getMappingWarehouseCode, mappingWarehouseCode)
                .eq(StringUtils.isNotBlank(mappingWarehouseName), HtpWarehouseMapping::getMappingWarehouseName, mappingWarehouseName)
        );
    }

    /**
     * 新增仓库与仓库关联映射模块
     *
     * @param htpWarehouseMappingDTO 仓库与仓库关联映射模块
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHtpWarehouseMapping(HtpWarehouseMappingDTO htpWarehouseMappingDTO) {
        HtpWarehouseMapping htpWarehouseMapping = new HtpWarehouseMapping();
        checkBeforeInsert(htpWarehouseMappingDTO);
        BeanUtils.copyProperties(htpWarehouseMappingDTO, htpWarehouseMapping);
        return baseMapper.insert(htpWarehouseMapping);
    }

    private void checkBeforeInsert(HtpWarehouseMappingDTO htpWarehouseMappingDTO) {
        Integer integer = baseMapper.selectCount(Wrappers.<HtpWarehouseMapping>lambdaQuery()
                .ne(htpWarehouseMappingDTO.getId() != null, HtpWarehouseMapping::getId, htpWarehouseMappingDTO.getId())
                .eq(HtpWarehouseMapping::getWarehouseCode, htpWarehouseMappingDTO.getWarehouseCode())
                .eq(HtpWarehouseMapping::getMappingWarehouseCode, htpWarehouseMappingDTO.getMappingWarehouseCode())
        );
        Assert.isTrue(integer == 0, "已存在相同的仓库映射配置");
    }

    /**
     * 修改仓库与仓库关联映射模块
     *
     * @param htpWarehouseMappingDTO 仓库与仓库关联映射模块
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHtpWarehouseMapping(HtpWarehouseMappingDTO htpWarehouseMappingDTO) {
        Assert.notNull(htpWarehouseMappingDTO.getId(), "id is require");
        HtpWarehouseMapping htpWarehouseMapping = new HtpWarehouseMapping();
        checkBeforeInsert(htpWarehouseMappingDTO);
        BeanUtils.copyProperties(htpWarehouseMappingDTO, htpWarehouseMapping);
        return baseMapper.updateById(htpWarehouseMapping);
    }

    /**
     * 批量删除仓库与仓库关联映射模块
     *
     * @param ids 需要删除的仓库与仓库关联映射模块ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHtpWarehouseMappingByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除仓库与仓库关联映射模块信息
     *
     * @param id 仓库与仓库关联映射模块ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHtpWarehouseMappingById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HtpWarehouseMappingVO changeStatus(Integer id, Integer status) {
        HtpWarehouseMappingVO htpWarehouseMappingVO = baseMapper.selectOneById(id);
        int update = baseMapper.update(new HtpWarehouseMapping(), Wrappers.<HtpWarehouseMapping>lambdaUpdate()
                .eq(HtpWarehouseMapping::getId, id)
                .set(HtpWarehouseMapping::getStatus, status));
        return htpWarehouseMappingVO;
    }
}

