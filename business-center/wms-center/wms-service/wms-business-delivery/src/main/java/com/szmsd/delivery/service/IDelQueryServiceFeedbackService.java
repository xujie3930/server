package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelQueryServiceFeedback;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * 查件服务反馈 服务类
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
public interface IDelQueryServiceFeedbackService extends IService<DelQueryServiceFeedback> {

        /**
        * 查询查件服务反馈模块
        *
        * @param id 查件服务反馈模块ID
        * @return 查件服务反馈模块
        */
        DelQueryServiceFeedback selectDelQueryServiceFeedbackById(String id);

        /**
        * 查询查件服务反馈模块列表
        *
        * @param delQueryServiceFeedback 查件服务反馈模块
        * @return 查件服务反馈模块集合
        */
        List<DelQueryServiceFeedback> selectDelQueryServiceFeedbackList(DelQueryServiceFeedback delQueryServiceFeedback);

        /**
        * 新增查件服务反馈模块
        *
        * @param delQueryServiceFeedback 查件服务反馈模块
        * @return 结果
        */
        int insertDelQueryServiceFeedback(DelQueryServiceFeedback delQueryServiceFeedback);

        /**
        * 修改查件服务反馈模块
        *
        * @param delQueryServiceFeedback 查件服务反馈模块
        * @return 结果
        */
        int updateDelQueryServiceFeedback(DelQueryServiceFeedback delQueryServiceFeedback);

        /**
        * 批量删除查件服务反馈模块
        *
        * @param ids 需要删除的查件服务反馈模块ID
        * @return 结果
        */
        int deleteDelQueryServiceFeedbackByIds(List<String> ids);

        /**
        * 删除查件服务反馈模块信息
        *
        * @param id 查件服务反馈模块ID
        * @return 结果
        */
        int deleteDelQueryServiceFeedbackById(String id);

}

