package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.dto.BasSellerDto;
import com.szmsd.bas.dto.BasSellerInfoDto;
import com.szmsd.common.core.domain.R;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
*
* @author l
* @since 2021-03-09
*/
public interface IBasSellerService extends IService<BasSeller> {

        /**
        * 查询模块
        *
        * @param id 模块ID
        * @return 模块
        */
        BasSeller selectBasSellerById(String id);

        /**
        * 查询模块列表
        *
        * @param basSeller 模块
        * @return 模块集合
        */
        List<BasSeller> selectBasSellerList(BasSeller basSeller);

        /**
         * 查询sellerCode
         * @param basSeller
         * @return
         */
        String getSellerCode(BasSeller basSeller);

        /**
        * 新增模块
        *
        * @param dto 模块
        * @return 结果
        */
        R<Boolean> insertBasSeller(HttpServletRequest request, BasSellerDto dto);

        /**
         * 用户名查询用户信息
         * @param userName
         * @return
         */
        BasSellerInfoDto selectBasSeller(String userName);

        /**
         * 获取验证码
         * @return
         */
        R getCheckCode(HttpServletRequest request);

        /**
        * 修改模块
        *
        * @param basSellerInfoDto 模块
        * @return 结果
        */
        int updateBasSeller(BasSellerInfoDto basSellerInfoDto) throws IllegalAccessException;

        /**
        * 批量删除模块
        *
        * @param ids 需要删除的模块ID
        * @return 结果
        */
        boolean deleteBasSellerByIds(List<Long> ids) throws IllegalAccessException;

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBasSellerById(String id);

}

