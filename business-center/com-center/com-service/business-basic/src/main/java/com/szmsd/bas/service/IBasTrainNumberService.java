package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasTrainNumber;

import java.util.List;

/**
 * <p>
 * 车次表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-11-18
 */
public interface IBasTrainNumberService extends IService<BasTrainNumber> {

    /**
     * 查询车次表模块
     *
     * @param id 车次表模块ID
     * @return 车次表模块
     */
    public BasTrainNumber selectBasTrainNumberById(String id);

    /**
     * 查询车次表模块列表
     *
     * @param BasTrainNumber 车次表模块
     * @return 车次表模块集合
     */
    public List<BasTrainNumber> selectBasTrainNumberList(BasTrainNumber basTrainNumber);

    /**
     * 新增车次表模块
     *
     * @param BasTrainNumber 车次表模块
     * @return 结果
     */
    public int insertBasTrainNumber(BasTrainNumber basTrainNumber);

    /**
     * 修改车次表模块
     *
     * @param BasTrainNumber 车次表模块
     * @return 结果
     */
    public int updateBasTrainNumber(BasTrainNumber basTrainNumber);

    /**
     * 批量删除车次表模块
     *
     * @param ids 需要删除的车次表模块ID
     * @return 结果
     */
    public int deleteBasTrainNumberByIds(List<String> ids);

    /**
     * 删除车次表模块信息
     *
     * @param id 车次表模块ID
     * @return 结果
     */
    public int deleteBasTrainNumberById(String id);
}
