package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasSkuRuleMatching;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.dto.BasSkuRuleMatchingDto;
import com.szmsd.bas.dto.BasSkuRuleMatchingImportDto;

import java.util.List;

/**
* <p>
    * sku规则匹配表 服务类
    * </p>
*
* @author Administrator
* @since 2022-05-10
*/
public interface IBasSkuRuleMatchingService extends IService<BasSkuRuleMatching> {

        /**
        * 查询sku规则匹配表模块列表
        *
        * @param basSkuRuleMatching sku规则匹配表模块
        * @return sku规则匹配表模块集合
        */
        List<BasSkuRuleMatching> selectBasSkuRuleMatchingList(BasSkuRuleMatching basSkuRuleMatching);

        /**
        * 新增、修改sku规则匹配表模块
        *
        * @param basSkuRuleMatching sku规则匹配表模块
        * @return 结果
        */
        int updateBasSkuRuleMatching(BasSkuRuleMatching basSkuRuleMatching);

        /**
        * 批量删除sku规则匹配表模块
        *
        * @param ids 需要删除的sku规则匹配表模块ID
        * @return 结果
        */
        int deleteBasSkuRuleMatchingByIds(List<BasSkuRuleMatching>  list);

    void importBaseProduct(List<BasSkuRuleMatchingImportDto> userList, String sellerCode);

    List<BasSkuRuleMatching> getList(BasSkuRuleMatchingDto dto);
}

