package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasWeightSection;
import com.szmsd.bas.domain.dto.BasWeightSectionDto;
import com.szmsd.bas.domain.dto.BasWeightSectionQueryDto;
import com.szmsd.bas.domain.vo.BasWeightSectionVo;

import java.util.List;

/**
* <p>
    * 重量区间设置 服务类
    * </p>
*
* @author 2
* @since 2021-01-11
*/
public interface IBasWeightSectionService extends IService<BasWeightSection> {

        /**
        * 查询重量区间设置模块列表
        *
        * @param queryDto 重量区间设置模块
        * @return 重量区间设置模块集合
        */
        List<BasWeightSectionVo> selectBasWeightSectionList(BasWeightSectionQueryDto queryDto);

        /**
        * 新增重量区间设置模块
        *
        * @param dto 重量区间设置模块
        * @return 结果
        */
        int insertBasWeightSection(BasWeightSectionDto dto);

        /**
         * 删除重量段
         * @param dto
         * @return
         */
        int deleteBasWeightSection(BasWeightSectionDto dto);

}

