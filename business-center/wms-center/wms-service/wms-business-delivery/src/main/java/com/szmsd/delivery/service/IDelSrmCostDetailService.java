package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelSrmCostDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * 出库单SRC成本明细 服务类
    * </p>
*
* @author Administrator
* @since 2022-03-04
*/
public interface IDelSrmCostDetailService extends IService<DelSrmCostDetail> {

        /**
        * 查询出库单SRC成本明细模块
        *
        * @param id 出库单SRC成本明细模块ID
        * @return 出库单SRC成本明细模块
        */
        DelSrmCostDetail selectDelSrmCostDetailById(String id);

        /**
        * 查询出库单SRC成本明细模块列表
        *
        * @param delSrmCostDetail 出库单SRC成本明细模块
        * @return 出库单SRC成本明细模块集合
        */
        List<DelSrmCostDetail> selectDelSrmCostDetailList(DelSrmCostDetail delSrmCostDetail);

        /**
        * 新增出库单SRC成本明细模块
        *
        * @param delSrmCostDetail 出库单SRC成本明细模块
        * @return 结果
        */
        int insertDelSrmCostDetail(DelSrmCostDetail delSrmCostDetail);

        /**
        * 修改出库单SRC成本明细模块
        *
        * @param delSrmCostDetail 出库单SRC成本明细模块
        * @return 结果
        */
        int updateDelSrmCostDetail(DelSrmCostDetail delSrmCostDetail);

        /**
        * 批量删除出库单SRC成本明细模块
        *
        * @param ids 需要删除的出库单SRC成本明细模块ID
        * @return 结果
        */
        int deleteDelSrmCostDetailByIds(List<String> ids);

        /**
        * 删除出库单SRC成本明细模块信息
        *
        * @param id 出库单SRC成本明细模块ID
        * @return 结果
        */
        int deleteDelSrmCostDetailById(String id);

    DelSrmCostDetail getByOrderNo(String orderNo);
}

