package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasEmployees;
import com.szmsd.common.core.domain.R;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-06-19
 */
public interface IBasEmployeesService extends IService<BasEmployees> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasEmployees selectBasEmployeesById(String id);

    /**
     * 查询模块列表
     *
     * @param BasEmployees 模块
     * @return 模块集合
     */
    public List<BasEmployees> selectBasEmployeesList(BasEmployees basEmployees);

    /**
     * 新增模块
     *
     * @param BasEmployees 模块
     * @return 结果
     */
    public int insertBasEmployees(BasEmployees basEmployees);

    /**
     * 修改模块
     *
     * @param BasEmployees 模块
     * @return 结果
     */
    public int updateBasEmployees(BasEmployees basEmployees);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasEmployeesByIds(List<String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasEmployeesById(String id);

    /**
     * 根据 code 获取员工信息
     * @param basEmployees
     * @return
     */
    R<BasEmployees> getEmpByCode(BasEmployees basEmployees);
}
