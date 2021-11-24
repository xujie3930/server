package com.szmsd.exception.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.exception.domain.ExceptionInfo;
import com.szmsd.exception.dto.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author l
* @since 2021-03-30
*/
public interface IExceptionInfoService extends IService<ExceptionInfo> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        ExceptionInfo selectExceptionInfoById(String id);

        /**
        * 查询模块列表
        *
        * @param exceptionInfo 模块
        * @return 模块集合
        */
        List<ExceptionInfo> selectExceptionInfoList(ExceptionInfo exceptionInfo);

        /**
         * 查询模块列表
         *
         * @param dto 模块
         * @return 模块集合
         */
        List<ExceptionInfo> selectExceptionInfoPage(ExceptionInfoQueryDto dto);

        /**
        * 新增模块
        *
        * @param newExceptionRequest 模块
        * @return 结果
        */
        void insertExceptionInfo(NewExceptionRequest newExceptionRequest);

        /**
         * 记录处理情况
         * @param processExceptionRequest
         */
        void processExceptionInfo(@RequestBody ProcessExceptionRequest processExceptionRequest);

        /**
        * 修改模块
        *
        * @param exceptionInfo 模块
        * @return 结果
        */
        int updateExceptionInfo(ExceptionInfoDto exceptionInfo);

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        int deleteExceptionInfoByIds(List<String> ids);

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteExceptionInfoById(String id);

        /**
         * 重新获取挂号
         * @param dto dto
         * @return int
         */
        int againTrackingNo(ExceptionDelOutboundAgainTrackingNoDto dto);

        /**
         * 忽略异常
         * @param exceptionInfo exceptionInfo
         * @return int
         */
        int ignore(ExceptionInfoDto exceptionInfo);
}

