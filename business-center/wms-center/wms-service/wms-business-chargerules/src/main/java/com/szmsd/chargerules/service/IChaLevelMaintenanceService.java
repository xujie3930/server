package com.szmsd.chargerules.service;

import com.szmsd.chargerules.domain.ChaLevelMaintenanceDtoQuery;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author admin
* @since 2022-06-22
*/
public interface IChaLevelMaintenanceService {


        /**
        * 查询模块列表
        *
        * @param chaLevelMaintenance 模块
        * @return 模块集合
        */
        TableDataInfo<ChaLevelMaintenanceDto> selectChaLevelMaintenanceList(ChaLevelMaintenanceDtoQuery chaLevelMaintenance);

        R<List<ChaLevelMaintenanceDto>> allList(ChaLevelMaintenanceDto chaLevelMaintenance);



        /**
        * 新增模块
        *
        * @param chaLevelMaintenance 模块
        * @return 结果
        */
        R insertChaLevelMaintenance(ChaLevelMaintenanceDto chaLevelMaintenance);

        /**
        * 修改模块
        *
        * @param chaLevelMaintenance 模块
        * @return 结果
        */
        R updateChaLevelMaintenance(ChaLevelMaintenanceDto chaLevelMaintenance);


        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        R deleteChaLevelMaintenanceById(String id);


        /**
         * 查询模块
         *
         * @param id 模块ID
         * @return 模块
         */
        R<ChaLevelMaintenanceDto> selectChaLevelMaintenanceById(String id);

}

