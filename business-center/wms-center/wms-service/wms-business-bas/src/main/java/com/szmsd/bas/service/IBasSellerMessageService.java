package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasSellerMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author l
* @since 2021-04-25
*/
public interface IBasSellerMessageService extends IService<BasSellerMessage> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        BasSellerMessage selectBasSellerMessageById(String id);

        /**
        * 查询模块列表
        *
        * @param basSellerMessage 模块
        * @return 模块集合
        */
        List<BasSellerMessage> selectBasSellerMessageList(BasSellerMessage basSellerMessage);

        /**
        * 新增模块
        *
        * @param basSellerMessage 模块
        * @return 结果
        */
        int insertBasSellerMessage(BasSellerMessage basSellerMessage);

        /**
         * 新增消息通知
         * @param id
         * @return
         */
        void insertBasSellerMessage(Long id,Boolean bullet);

        /**
        * 修改模块
        *
        * @param basSellerMessage 模块
        * @return 结果
        */
        int updateBasSellerMessage(BasSellerMessage basSellerMessage);

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        int deleteBasSellerMessageByIds(List<String> ids);

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBasSellerMessageById(String id);

}

