package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasMes;
import com.szmsd.bas.domain.Mes;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-08-20
 */
public interface IBasMesService extends IService<BasMes> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasMes selectBasMesById(String id);

    /**
     * 查询模块列表
     *
     * @param BasMes 模块
     * @return 模块集合
     */
    public List<BasMes> selectBasMesList(BasMes basMes);

    /**
     * 新增模块
     *
     * @param BasMes 模块
     * @return 结果
     */
    public int insertBasMes(BasMes basMes);

    /**
     * 修改模块
     *
     * @param BasMes 模块
     * @return 结果
     */
    public int updateBasMes(BasMes basMes);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasMesByIds(List<String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasMesById(String id);

    List<Mes> list(Mes basMes);
}
