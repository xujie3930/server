package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasCar;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-09-24
 */
public interface IBasCarService extends IService<BasCar> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasCar selectBasCarById(String id);

    /**
     * 查询模块列表
     *
     * @param BasCar 模块
     * @return 模块集合
     */
    public List<BasCar> selectBasCarList(BasCar basCar);

    /**
     * 新增模块
     *
     * @param BasCar 模块
     * @return 结果
     */
    public int insertBasCar(BasCar basCar);

    /**
     * 修改模块
     *
     * @param BasCar 模块
     * @return 结果
     */
    public int updateBasCar(BasCar basCar);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasCarByIds(List
                                         <String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasCarById(String id);
}
