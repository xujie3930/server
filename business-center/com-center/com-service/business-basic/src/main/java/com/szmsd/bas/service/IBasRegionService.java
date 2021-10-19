package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasRegionQueryDTO;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;

import java.util.List;

/**
 * <p>
 * 地区信息 服务类
 * </p>
 *
 * @author gen
 * @since 2020-11-13
 */
public interface IBasRegionService extends IService<BasRegion> {

    /**
     * 查询地区信息模块
     *
     * @param id 地区信息模块ID
     * @return 地区信息模块
     */
    BasRegion selectBasRegionById(String id);

    /**
     * 查询地区信息模块列表
     *
     * @param basRegion 地区信息模块
     * @return 地区信息模块集合
     */
    List<BasRegion> selectBasRegionList(BasRegionQueryDTO basRegion);

    /**
     * 查询国家
     * @param
     * @return
     */
    List<BasRegion> listCountry();

    /**
     * 新增地区信息模块
     *
     * @param basRegion 地区信息模块
     * @return 结果
     */
    int insertBasRegion(BasRegion basRegion);

    /**
     * 修改地区信息模块
     *
     * @param basRegion 地区信息模块
     * @return 结果
     */
    int updateBasRegion(BasRegion basRegion);

    /**
     * 批量删除地区信息模块
     *
     * @param ids 需要删除的地区信息模块ID
     * @return 结果
     */
    int deleteBasRegionByIds(List<String> ids);

    /**
     * 删除地区信息模块信息
     *
     * @param id 地区信息模块ID
     * @return 结果
     */
    int deleteBasRegionById(String id);

    /**
     * 下拉选择
     *
     * @param queryDto queryDto
     * @return BasRegionSelectListVO
     */
    List<BasRegionSelectListVO> selectList(BasRegionSelectListQueryDto queryDto);

}
