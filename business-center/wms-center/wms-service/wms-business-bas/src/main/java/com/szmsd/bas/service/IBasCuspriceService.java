package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasCusprice;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-06-29
 */
public interface IBasCuspriceService extends IService<BasCusprice> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasCusprice selectBasCuspriceById(String id);

    /**
     * 查询模块列表
     *
     * @param BasCusprice 模块
     * @return 模块集合
     */
    public List<BasCusprice> selectBasCuspriceList(BasCusprice basCusprice);

    /**
     * 新增模块
     *
     * @param BasCusprice 模块
     * @return 结果
     */
    public int insertBasCusprice(BasCusprice basCusprice);

    /**
     * 修改模块
     *
     * @param BasCusprice 模块
     * @return 结果
     */
    public int updateBasCusprice(BasCusprice basCusprice);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasCuspriceByIds(List
                                              <String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasCuspriceById(String id);
}
