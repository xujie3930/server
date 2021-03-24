package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.api.domain.BasKeyword;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-06-13
 */
public interface IBasKeywordService extends IService<BasKeyword> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasKeyword selectBasKeywordById(String id);

    /**
     * 查询模块列表
     *
     * @param BasKeyword 模块
     * @return 模块集合
     */
    public List<BasKeyword> selectBasKeywordList(BasKeyword basKeyword);

    /**
     * 新增模块
     *
     * @param BasKeyword 模块
     * @return 结果
     */
    public int insertBasKeyword(BasKeyword basKeyword);

    /**
     * 修改模块
     *
     * @param BasKeyword 模块
     * @return 结果
     */
    public int updateBasKeyword(BasKeyword basKeyword);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasKeywordByIds(List<String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasKeywordById(String id);


    /**
     * 根据目的地删除关键字
     * @param siteCode
     * @return
     */
    int deleteBydestination(String siteCode);


}
