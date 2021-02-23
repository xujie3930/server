package com.szmsd.demo.service;

import com.szmsd.demo.domain.Test;
import com.szmsd.system.api.domain.SysUser;

import java.util.List;

public interface TestService {
    /**
     * 查询测试模块
     *
     * @param id 测试模块ID
     * @return 测试模块
     */
     Test selectTestById(Long id);

    /**
     * 查询测试模块列表
     *
     * @param test 测试模块
     * @return 测试模块集合
     */
     List<Test> selectTestList(Test test);

    /**
     * 新增测试模块
     *
     * @param test 测试模块
     * @return 结果
     */
     int insertTest(Test test);

    /**
     * 修改测试模块
     *
     * @param test 测试模块
     * @return 结果
     */
     int updateTest(Test test);

    /**
     * 批量删除测试模块
     *
     * @param ids 需要删除的测试模块ID
     * @return 结果
     */
     int deleteTestByIds(List<Long> ids);

    /**
     * 删除测试模块信息
     *
     * @param id 测试模块ID
     * @return 结果
     */
     int deleteTestById(Long id);

    /**
     * 导入测试模块数据
     *
     * @param testList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importTest(List<Test> testList, Boolean isUpdateSupport, String operName);

}
