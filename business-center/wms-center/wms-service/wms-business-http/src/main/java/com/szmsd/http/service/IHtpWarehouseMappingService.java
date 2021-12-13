package com.szmsd.http.service;

import com.szmsd.http.domain.HtpWarehouseMapping;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingDTO;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingQueryDTO;
import com.szmsd.http.vo.mapping.HtpWarehouseMappingVO;

import java.util.List;

/**
 * <p>
 * 仓库与仓库关联映射 服务类
 * </p>
 *
 * @author 11
 * @since 2021-12-13
 */
public interface IHtpWarehouseMappingService extends IService<HtpWarehouseMapping> {

    /**
     * 查询仓库与仓库关联映射模块
     *
     * @param id 仓库与仓库关联映射模块ID
     * @return 仓库与仓库关联映射模块
     */
    HtpWarehouseMappingVO selectHtpWarehouseMappingById(Integer id);

    /**
     * 查询仓库与仓库关联映射模块列表
     *
     * @param htpWarehouseMapping 仓库与仓库关联映射模块
     * @return 仓库与仓库关联映射模块集合
     */
    List<HtpWarehouseMappingVO> selectHtpWarehouseMappingList(HtpWarehouseMappingQueryDTO htpWarehouseMapping);

    /**
     * 新增仓库与仓库关联映射模块
     *
     * @param htpWarehouseMapping 仓库与仓库关联映射模块
     * @return 结果
     */
    int insertHtpWarehouseMapping(HtpWarehouseMappingDTO htpWarehouseMapping);

    /**
     * 修改仓库与仓库关联映射模块
     *
     * @param htpWarehouseMapping 仓库与仓库关联映射模块
     * @return 结果
     */
    int updateHtpWarehouseMapping(HtpWarehouseMappingDTO htpWarehouseMapping);

    /**
     * 批量删除仓库与仓库关联映射模块
     *
     * @param ids 需要删除的仓库与仓库关联映射模块ID
     * @return 结果
     */
    int deleteHtpWarehouseMappingByIds(List<String> ids);

    /**
     * 删除仓库与仓库关联映射模块信息
     *
     * @param id 仓库与仓库关联映射模块ID
     * @return 结果
     */
    int deleteHtpWarehouseMappingById(String id);

    HtpWarehouseMappingVO changeStatus(Integer id, Integer status);
}

