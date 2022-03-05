package com.szmsd.delivery.service;

import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelSrmCostLog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
* <p>
    * 出库单SRC成本调用日志 服务类
    * </p>
*
* @author Administrator
* @since 2022-03-04
*/
public interface IDelSrmCostLogService extends IService<DelSrmCostLog> {

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_CK1_REQUEST)
    void handler(DelSrmCostLog ck1RequestLog);

    /**
        * 查询出库单SRC成本调用日志模块
        *
        * @param id 出库单SRC成本调用日志模块ID
        * @return 出库单SRC成本调用日志模块
        */
        DelSrmCostLog selectDelSrmCostLogById(String id);

        /**
        * 查询出库单SRC成本调用日志模块列表
        *
        * @param delSrmCostLog 出库单SRC成本调用日志模块
        * @return 出库单SRC成本调用日志模块集合
        */
        List<DelSrmCostLog> selectDelSrmCostLogList(DelSrmCostLog delSrmCostLog);

        /**
        * 新增出库单SRC成本调用日志模块
        *
        * @param delSrmCostLog 出库单SRC成本调用日志模块
        * @return 结果
        */
        int insertDelSrmCostLog(DelSrmCostLog delSrmCostLog);

        /**
        * 修改出库单SRC成本调用日志模块
        *
        * @param delSrmCostLog 出库单SRC成本调用日志模块
        * @return 结果
        */
        int updateDelSrmCostLog(DelSrmCostLog delSrmCostLog);

        /**
        * 批量删除出库单SRC成本调用日志模块
        *
        * @param ids 需要删除的出库单SRC成本调用日志模块ID
        * @return 结果
        */
        int deleteDelSrmCostLogByIds(List<String> ids);

        /**
        * 删除出库单SRC成本调用日志模块信息
        *
        * @param id 出库单SRC成本调用日志模块ID
        * @return 结果
        */
        int deleteDelSrmCostLogById(String id);

}

