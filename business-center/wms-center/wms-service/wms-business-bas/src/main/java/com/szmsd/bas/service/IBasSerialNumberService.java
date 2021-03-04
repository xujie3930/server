package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasSerialNumber;

import java.util.List;

/**
 * <p>
 * 流水号信息 服务类
 * </p>
 *
 * @author gen
 * @since 2020-11-10
 */
public interface IBasSerialNumberService extends IService<BasSerialNumber> {

    /**
     * 查询流水号信息模块
     *
     * @param id 流水号信息模块ID
     * @return 流水号信息模块
     */
    BasSerialNumber selectBaseSerialNumberById(String id);

    /**
     * 查询流水号信息模块列表
     *
     * @param baseSerialNumber 流水号信息模块
     * @return 流水号信息模块集合
     */
    List<BasSerialNumber> selectBaseSerialNumberList(BasSerialNumber baseSerialNumber);

    /**
     * 新增流水号信息模块
     *
     * @param baseSerialNumber 流水号信息模块
     * @return 结果
     */
    int insertBaseSerialNumber(BasSerialNumber baseSerialNumber);

    /**
     * 修改流水号信息模块
     *
     * @param baseSerialNumber 流水号信息模块
     * @return 结果
     */
    int updateBaseSerialNumber(BasSerialNumber baseSerialNumber);

    /**
     * 批量删除流水号信息模块
     *
     * @param ids 需要删除的流水号信息模块ID
     * @return 结果
     */
    int deleteBaseSerialNumberByIds(List<String> ids);

    /**
     * 删除流水号信息模块信息
     *
     * @param id 流水号信息模块ID
     * @return 结果
     */
    int deleteBaseSerialNumberById(String id);

    /**
     * 生成流水号
     *
     * @param code 业务编码
     * @return string
     */
    String generateNumber(String code);

    /**
     * 批量生成流水号
     *
     * @param code 业务编码
     * @param num  生成个数
     * @return string
     */
    List<String> generateNumbers(String code, int num);
}
