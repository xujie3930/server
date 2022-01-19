package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.bas.vo.BasSellerWrapVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;

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
        TableDataInfo<BasSellerSysDto> selectBasSellerList(BasSellerQueryDto basSeller);

        /**
         * 查询模块列表
         *
         * @param basSeller 模块
         * @return 模块集合
         */
        List<BasSellerSysDto> getBasSellerList(BasSeller basSeller);


        /**
         * 查询sellerCode
         * @param basSeller
         * @return
         */
        String getSellerCode(BasSeller basSeller);

        /**
         * 查询sellerCode
         * @param
         * @return
         */
        String getLoginSellerCode();

        String getInspection(String sellerCode);

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
        BasSellerInfoVO selectBasSeller(String userName);

    BasSellerInfoVO selectBasSellerBySellerCode(String sellerCode);

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
        * @param activeDto 需要删除的模块ID
        * @return 结果
        */
        boolean deleteBasSellerByIds(ActiveDto activeDto) throws IllegalAccessException;

        /**
        * 删除模块信息
        *
        * @param id 模块ID
        * @return 结果
        */
        int deleteBasSellerById(String id);

        List<String> getAllSellerCode();

    /**
     * 查询业务经理/客服下所属的客户编码
     * @param conditionDto conditionDto
     * @return String
     */
    List<String> queryByServiceCondition(ServiceConditionDto conditionDto);

    List<BasSellerEmailDto> queryAllSellerCodeAndEmail();

    /**
     * 查询实名状态
     * @param sellerCode sellerCode
     * @return String
     */
    String getRealState(String sellerCode);

    /**
     * 查询是否需要推送CK1 判断
     * @param sellerCode 用户code
     * @return false 不推送
     */
    BasSellerWrapVO queryCkPushFlag(String sellerCode);


    void updateUserInfoForMan();
}

