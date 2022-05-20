package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasOtherRules;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * 其他规则匹配 服务类
    * </p>
*
* @author Administrator
* @since 2022-05-16
*/
public interface IBasOtherRulesService extends IService<BasOtherRules> {

        /**
        * 查询其他规则匹配模块
        *
         * @param sellerCode 客户编号
        * @return 其他规则匹配模块
        */
        BasOtherRules selectBasOtherRulesById(String sellerCode);

        /**
        * 查询其他规则匹配模块列表
        *
        * @param basOtherRules 其他规则匹配模块
        * @return 其他规则匹配模块集合
        */
        List<BasOtherRules> selectBasOtherRulesList(BasOtherRules basOtherRules);

        /**
        * 新增其他规则匹配模块
        *
        * @param basOtherRules 其他规则匹配模块
        * @return 结果
        */
        int addOrUpdate(BasOtherRules basOtherRules);

        /**
        * 修改其他规则匹配模块
        *
        * @param basOtherRules 其他规则匹配模块
        * @return 结果
        */
        int updateBasOtherRules(BasOtherRules basOtherRules);

        /**
        * 批量删除其他规则匹配模块
        *
        * @param ids 需要删除的其他规则匹配模块ID
        * @return 结果
        */
        int deleteBasOtherRulesByIds(List<String> ids);

        /**
        * 删除其他规则匹配模块信息
        *
        * @param id 其他规则匹配模块ID
        * @return 结果
        */
        int deleteBasOtherRulesById(String id);

}

