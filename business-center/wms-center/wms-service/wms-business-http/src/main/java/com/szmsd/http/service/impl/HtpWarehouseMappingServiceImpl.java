package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.Page;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.http.domain.HtpWarehouseMapping;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingDTO;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingQueryDTO;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.mapper.HtpWarehouseMappingMapper;
import com.szmsd.http.service.IHtpWarehouseMappingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.http.service.RemoteInterfaceService;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.http.vo.mapping.CkWarehouseMappingVO;
import com.szmsd.http.vo.mapping.HtpWarehouseMappingVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Resource
    private RemoteInterfaceService remoteInterfaceService;

    private final static String CACHE_KEY = "http:WarMapping:";

    /**
     * 查询仓库与仓库关联映射模块
     *
     * @param id 仓库与仓库关联映射模块ID
     * @return 仓库与仓库关联映射模块
     */
    @Override
    @Cacheable(value = CACHE_KEY + "getInfo", key = "#id")
    public HtpWarehouseMappingVO selectHtpWarehouseMappingById(Integer id) {
        return baseMapper.selectOneById(id);
    }

    @Cacheable(value = CACHE_KEY + "ckWarList")
    public List<HtpWarehouseMappingVO> getCkWarList() {
        HttpRequestDto dto = new HttpRequestDto();
        dto.setMethod(HttpMethod.GET);
        dto.setUri("${" + DomainEnum.Ck1OpenAPIDomain.name() + "}/v1/warehouses");
        dto.setBinary(false);
        HttpResponseVO data = remoteInterfaceService.rmi(dto);
        String responseStr = (String) data.getBody();
        List<CkWarehouseMappingVO> htpWarehouseMappings = JSONObject.parseArray(responseStr, CkWarehouseMappingVO.class);
        return htpWarehouseMappings.stream().map(HtpWarehouseMappingVO::new).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CACHE_KEY + "mappingWarCode", key = "#warehouseCode")
    public String getMappingWarCode(String warehouseCode) {
        HtpWarehouseMapping htpWarehouseMapping = baseMapper.selectOne(Wrappers.<HtpWarehouseMapping>lambdaQuery()
                .eq(HtpWarehouseMapping::getWarehouseCode, warehouseCode)
                .select(HtpWarehouseMapping::getMappingWarehouseCode));
        AssertUtil.notNull(htpWarehouseMapping, "未配置" + warehouseCode + "的仓库映射配置");
        return htpWarehouseMapping.getMappingWarehouseCode();
    }

    /**
     * 查询条件
     *
     * @param htpWarehouseMapping 仅查询code 和 name
     * @return
     */
    @Override
    @Cacheable(value = CACHE_KEY + "ckList", key = "#htpWarehouseMapping")
    public List<HtpWarehouseMappingVO> ckList(HtpWarehouseMappingQueryDTO htpWarehouseMapping) {

        List<HtpWarehouseMappingVO> result = getCkWarList();

        String mappingWarehouseCode = htpWarehouseMapping.getMappingWarehouseCode();
        String mappingWarehouseName = htpWarehouseMapping.getMappingWarehouseName();
        boolean queryCode = StringUtils.isNotBlank(mappingWarehouseCode);
        boolean queryName = StringUtils.isNotBlank(mappingWarehouseName);
        result = result.stream().filter(x -> {
            boolean isTrue = true;
            if (queryCode) {
                isTrue = isTrue && x.getMappingWarehouseCode().equals(mappingWarehouseCode);
            }
            if (queryName) {
                isTrue = isTrue && x.getMappingWarehouseName().equals(mappingWarehouseName);
            }
            return isTrue;
        }).collect(Collectors.toList());
        // 封装分页
        int pageNum = htpWarehouseMapping.getPageNum();
        int pageSize = htpWarehouseMapping.getPageSize();
        Page<HtpWarehouseMappingVO> localPage = new Page<>(pageNum, pageSize);
        localPage.setTotal(result.size());
        int startRow = Integer.min(localPage.getStartRow(), result.size());
        int endRow = Integer.min(localPage.getEndRow(), result.size());
        localPage.addAll(result.subList(startRow, endRow));
        return localPage;
    }

    /**
     * 查询仓库与仓库关联映射模块列表
     *
     * @param htpWarehouseMapping 仓库与仓库关联映射模块
     * @return 仓库与仓库关联映射模块
     */
    @Override
    @Cacheable(value = CACHE_KEY + "list", key = "#htpWarehouseMapping")
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
    @CacheEvict(value = CACHE_KEY + "*", allEntries = true)
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
    @CacheEvict(value = CACHE_KEY + "*", allEntries = true)
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
    @CacheEvict(value = CACHE_KEY + "*", allEntries = true)
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
    @CacheEvict(value = CACHE_KEY + "*", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public int deleteHtpWarehouseMappingById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    @CacheEvict(value = CACHE_KEY + "*", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public HtpWarehouseMappingVO changeStatus(Integer id, Integer status) {
        HtpWarehouseMappingVO htpWarehouseMappingVO = this.selectHtpWarehouseMappingById(id);
        int update = baseMapper.update(new HtpWarehouseMapping(), Wrappers.<HtpWarehouseMapping>lambdaUpdate()
                .eq(HtpWarehouseMapping::getId, id)
                .set(HtpWarehouseMapping::getStatus, status));
        return htpWarehouseMappingVO;
    }

}

