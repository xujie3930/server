package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasSysOperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author admin
* @since 2022-09-05
*/
public interface IBasSysOperationLogService extends IService<BasSysOperationLog> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        BasSysOperationLog selectBasSysOperationLogById(String id);

        /**
        * 查询模块列表
        *
        * @param basSysOperationLog 模块
        * @return 模块集合
        */
        List<BasSysOperationLog> selectBasSysOperationLogList(BasSysOperationLog basSysOperationLog);

        /**
        * 新增模块
        *
        * @param basSysOperationLog 模块
        * @return 结果
        */
        int insertBasSysOperationLog(BasSysOperationLog basSysOperationLog);

        /**
        * 修改模块
        *
        * @param basSysOperationLog 模块
        * @return 结果
        */
        int updateBasSysOperationLog(BasSysOperationLog basSysOperationLog);

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        int deleteBasSysOperationLogByIds(List<String> ids);

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBasSysOperationLogById(String id);

}

