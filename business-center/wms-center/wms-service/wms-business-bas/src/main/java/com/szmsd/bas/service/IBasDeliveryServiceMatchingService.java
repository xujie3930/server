package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasDeliveryServiceMatching;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.dto.BasDeliveryServiceMatchingDto;

import java.util.List;

/**
* <p>
    * 发货服务匹配 服务类
    * </p>
*
* @author Administrator
* @since 2022-05-12
*/
public interface IBasDeliveryServiceMatchingService extends IService<BasDeliveryServiceMatching> {

        /**
        * 查询发货服务匹配模块
        *
        * @param id 发货服务匹配模块ID
        * @return 发货服务匹配模块
        */
        BasDeliveryServiceMatching selectBasDeliveryServiceMatchingById(String id);

        /**
        * 查询发货服务匹配模块列表
        *
        * @param basDeliveryServiceMatching 发货服务匹配模块
        * @return 发货服务匹配模块集合
        */
        List<BasDeliveryServiceMatching> selectBasDeliveryServiceMatchingList(BasDeliveryServiceMatching basDeliveryServiceMatching);

        /**
        * 新增发货服务匹配模块
        *
        * @param basDeliveryServiceMatching 发货服务匹配模块
        * @return 结果
        */
        int insertBasDeliveryServiceMatching(BasDeliveryServiceMatching basDeliveryServiceMatching);

        /**
        * 修改发货服务匹配模块
        *
        * @param basDeliveryServiceMatching 发货服务匹配模块
        * @return 结果
        */
        int updateBasDeliveryServiceMatching(BasDeliveryServiceMatching basDeliveryServiceMatching);

        /**
        * 批量删除发货服务匹配模块
        *
        * @param ids 需要删除的发货服务匹配模块ID
        * @return 结果
        */
        int deleteBasDeliveryServiceMatchingByIds(List<String> ids);


        List<BasDeliveryServiceMatching> getList(BasDeliveryServiceMatchingDto dto);
}

