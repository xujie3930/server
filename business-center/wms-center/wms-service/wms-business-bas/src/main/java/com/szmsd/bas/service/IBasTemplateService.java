package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasTemplate;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-08-24
 */
public interface IBasTemplateService extends IService<BasTemplate> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasTemplate selectBasTemplateById(String id);

    /**
     * 查询模块列表
     *
     * @param BasTemplate 模块
     * @return 模块集合
     */
    public List<BasTemplate> selectBasTemplateList(BasTemplate basTemplate);

    /**
     * 新增模块
     *
     * @param BasTemplate 模块
     * @return 结果
     */
    public int insertBasTemplate(BasTemplate basTemplate);

    /**
     * 修改模块
     *
     * @param BasTemplate 模块
     * @return 结果
     */
    public int updateBasTemplate(BasTemplate basTemplate);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasTemplateByIds(List
                                              <String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasTemplateById(String id);
}
