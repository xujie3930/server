package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasCarrierKeyword;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author YM
 * @since 2022-01-24
 */
public interface IBasCarrierKeywordService extends IService<BasCarrierKeyword> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    BasCarrierKeyword selectBasCarrierKeywordById(String id);

    /**
     * 查询模块列表
     *
     * @param basCarrierKeyword 模块
     * @return 模块集合
     */
    List<BasCarrierKeyword> selectBasCarrierKeywordList(BasCarrierKeyword basCarrierKeyword);

    /**
     * 新增模块
     *
     * @param basCarrierKeyword 模块
     * @return 结果
     */
    int insertBasCarrierKeyword(BasCarrierKeyword basCarrierKeyword);

    /**
     * 修改模块
     *
     * @param basCarrierKeyword 模块
     * @return 结果
     */
    int updateBasCarrierKeyword(BasCarrierKeyword basCarrierKeyword);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    int deleteBasCarrierKeywordByIds(List<String> ids);

    /**
     * 检查是否包含关键词
     * @param carrierCode
     * @param text
     * @return
     */
    Boolean checkExistKeyword(String carrierCode, String text);
}

