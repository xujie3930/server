package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasProductType;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-06-13
 */
public interface IBasProductTypeService extends IService<BasProductType> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasProductType selectBasProductTypeById(String id);

    /**
     * 查询模块列表
     *
     * @param BasProductType 模块
     * @return 模块集合
     */
    public List<BasProductType> selectBasProductTypeList(BasProductType basProductType);

    /**
     * 新增模块
     *
     * @param BasProductType 模块
     * @return 结果
     */
    public int insertBasProductType(BasProductType basProductType);

    /**
     * 修改模块
     *
     * @param BasProductType 模块
     * @return 结果
     */
    public int updateBasProductType(BasProductType basProductType);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasProductTypeByIds(List
                                                 <String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasProductTypeById(String id);


    public List<BasProductType> selectGenTableColumnListByTableIds(List<String> id);
}