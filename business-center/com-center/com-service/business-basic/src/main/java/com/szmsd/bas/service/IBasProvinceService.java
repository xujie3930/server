package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasProvince;

import java.util.List;

/**
 * <p>
 * 省份表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-08-03
 */
public interface IBasProvinceService extends IService<BasProvince> {

    /**
     * 查询省份表模块
     *
     * @param id 省份表模块ID
     * @return 省份表模块
     */
    public BasProvince selectBasProvinceById(String id);

    /**
     * 查询省份表模块列表
     *
     * @param BasProvince 省份表模块
     * @return 省份表模块集合
     */
    public List<BasProvince> selectBasProvinceList(BasProvince basProvince);

    /**
     * 新增省份表模块
     *
     * @param BasProvince 省份表模块
     * @return 结果
     */
    public int insertBasProvince(BasProvince basProvince);

    /**
     * 修改省份表模块
     *
     * @param BasProvince 省份表模块
     * @return 结果
     */
    public int updateBasProvince(BasProvince basProvince);

    /**
     * 批量删除省份表模块
     *
     * @param ids 需要删除的省份表模块ID
     * @return 结果
     */
    public int deleteBasProvinceByIds(List
                                              <String> ids);

    /**
     * 删除省份表模块信息
     *
     * @param id 省份表模块ID
     * @return 结果
     */
    public int deleteBasProvinceById(String id);
}
