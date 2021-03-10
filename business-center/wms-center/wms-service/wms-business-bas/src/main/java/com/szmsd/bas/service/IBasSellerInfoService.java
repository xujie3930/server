package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasSellerInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author l
* @since 2021-03-09
*/
public interface IBasSellerInfoService extends IService<BasSellerInfo> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        BasSellerInfo selectBasSellerInfoById(String id);

        /**
        * 查询模块列表
        *
        * @param basSellerInfo 模块
        * @return 模块集合
        */
        List<BasSellerInfo> selectBasSellerInfoList(BasSellerInfo basSellerInfo);

        /**
        * 新增模块
        *
        * @param basSellerInfo 模块
        * @return 结果
        */
        int insertBasSellerInfo(BasSellerInfo basSellerInfo);

        /**
        * 修改模块
        *
        * @param basSellerInfo 模块
        * @return 结果
        */
        int updateBasSellerInfo(BasSellerInfo basSellerInfo);

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        int deleteBasSellerInfoByIds(List<String> ids);

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBasSellerInfoById(String id);

}

