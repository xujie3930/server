package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.dto.BasMessageDto;
import com.szmsd.bas.dto.BasMessageQueryDTO;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author l
* @since 2021-04-25
*/
public interface IBasMessageService extends IService<BasMessage> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        BasMessageDto selectBasMessageById(Long id);

        /**
        * 查询模块列表
        *
        * @param dto 模块
        * @return 模块集合
        */
        List<BasMessage> selectBasMessageList(BasMessageQueryDTO dto);

        /**
        * 新增模块
        *
        * @param basMessage 模块
        * @return 结果
        */
        void insertBasMessage(BasMessageDto basMessage);

        /**
        * 修改模块
        *
        * @param basMessage 模块
        * @return 结果
        */
        int updateBasMessage(BasMessage basMessage);

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        int deleteBasMessageByIds(List<Long> ids);

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBasMessageById(String id);

}

