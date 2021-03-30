package com.szmsd.returnex.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.returnex.domain.ReturnExpressDetail;
import com.szmsd.returnex.dto.*;
import com.szmsd.returnex.vo.ReturnExpressListVO;

import java.util.List;

/**
 * @ClassName: IReturnExpressService
 * @Description: 退货
 * @Author: 11
 * @Date: 2021/3/26 11:47
 */
public interface IReturnExpressService extends IService<ReturnExpressDetail> {

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    List<ReturnExpressListVO> selectReturnOrderList(ReturnExpressListQueryDTO queryDto);

    /**
     * 新建退件单
     *
     * @param returnExpressAddDTO 新增
     * @return 返回结果
     */
    int insertReturnExpressDetail(ReturnExpressAddDTO returnExpressAddDTO);

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    int saveArrivalInfoFormVms(ReturnArrivalReqDTO returnArrivalReqDTO);

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    int updateProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO);

    /**
     * 无名件管理列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    List<ReturnExpressListVO> pageForNoUserBind(ReturnExpressListQueryDTO queryDto);

    /**
     * 无名件批量指派客户
     *
     * @param expressAssignDTO 指派条件
     * @return 返回结果
     */
    int assignUsersForNoUserBindBatch(ReturnExpressAssignDTO expressAssignDTO);

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    int updateExpressInfo(ReturnExpressAddDTO expressUpdateDTO);

    /**
     * 新增退件单-生成预报单号
     *
     * @return 返回结果
     */
    String createExpectedNo();
}
