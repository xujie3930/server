package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingConditionQueryDto;
import com.szmsd.bas.dto.BasePackingQueryDto;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.dto.CreatePackingRequest;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author l
 * @since 2021-03-06
 */
public interface IBasePackingService extends IService<BasePacking> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    BasePacking selectBasePackingById(String id);

    /**
     * 查询模块列表
     *
     * @param basePacking 模块
     * @return 模块集合
     */
    List<BasePacking> selectBasePackingList(BasePacking basePacking);

    List<BasePacking> selectBasePackingPage(BasePackingQueryDto basePackingQueryDto);

    /**
     * 查询父类物料
     *
     * @return
     */
    List<BasePacking> selectBasePacking();


    /**
     * 新增模块
     *
     * @param basePacking 模块
     * @return 结果
     */
    int insertBasePacking(BasePacking basePacking);

    /**
     * 修改模块
     *
     * @param basePacking 模块
     * @return 结果
     */
    int updateBasePacking(BasePacking basePacking) throws IllegalAccessException;

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    int deleteBasePackingByIds(List<Long> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    int deleteBasePackingById(String id);

    /**
     * 根据仓库，SKU查询产品信息
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BaseProduct
     */
    List<BasePacking> queryPackingList(BaseProductConditionQueryDto conditionQueryDto);

    /**
     * 根据编码查询
     *
     * @param conditionQueryDto conditionQueryDto
     * @return BasePacking
     */
    BasePacking queryByCode(BasePackingConditionQueryDto conditionQueryDto);

    void createPackings(CreatePackingRequest createPackingRequest);
}

