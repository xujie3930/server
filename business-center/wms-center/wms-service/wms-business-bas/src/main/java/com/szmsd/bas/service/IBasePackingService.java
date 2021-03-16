package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingQueryDto;

import java.util.List;

/**
* <p>
    *  服务类
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
         * @return
         */
        List<BasePacking> selectBasePackingParent();

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

}

