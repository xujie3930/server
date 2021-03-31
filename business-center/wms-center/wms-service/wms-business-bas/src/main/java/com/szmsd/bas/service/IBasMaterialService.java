package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.dto.BasMaterialQueryDto;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author l
* @since 2021-03-12
*/
public interface IBasMaterialService extends IService<BasMaterial> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        BasMaterial selectBasMaterialById(String id);

        /**
        * 查询模块列表
        *
        * @param basMaterialQueryDto 模块
        * @return 模块集合
        */
        List<BasMaterial> selectBasMaterialList(BasMaterialQueryDto basMaterialQueryDto);

        /**
        * 新增模块
        *
        * @param basMaterial 模块
        * @return 结果
        */
        int insertBasMaterial(BasMaterial basMaterial);

        /**
        * 修改模块
        *
        * @param basMaterial 模块
        * @return 结果
        */
        int updateBasMaterial(BasMaterial basMaterial) throws IllegalAccessException;

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        boolean deleteBasMaterialByIds(List<Long> ids) throws IllegalAccessException;

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBasMaterialById(String id);

}

