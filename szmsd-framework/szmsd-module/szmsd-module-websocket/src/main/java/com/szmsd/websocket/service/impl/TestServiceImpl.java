package com.szmsd.websocket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.websocket.domain.Test;
import com.szmsd.websocket.mapper.TestMapper;
import com.szmsd.websocket.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    private static final Logger log = LoggerFactory.getLogger(TestServiceImpl.class);
    @Resource
    private TestMapper testMapper;

    /**
     * 查询测试模块
     *
     * @param id 测试模块ID
     * @return 测试模块
     */
    @Override
    public Test selectTestById(Long id) {
        return testMapper.selectById(id);

    }

    /**
     * 查询测试模块列表
     *
     * @param test 测试模块
     * @return 测试模块
     */
    @Override
    public List<Test> selectTestList(Test test) {
        return testMapper.selectList(new QueryWrapper<>(test));
    }

    /**
     * 新增测试模块
     *
     * @param test 测试模块
     * @return 结果
     */
    @Override
    public int insertTest(Test test) {
        return testMapper.insert(test);
    }

    /**
     * 修改测试模块
     *
     * @param test 测试模块
     * @return 结果
     */
    @Override
    public int updateTest(Test test) {
        return testMapper.updateById(test);
    }

    /**
     * 批量删除测试模块
     *
     * @param ids 需要删除的测试模块ID
     * @return 结果
     */
    @Override
    public int deleteTestByIds(List<Long> ids) {
        return testMapper.deleteBatchIds(ids);
    }

    /**
     * 删除测试模块信息
     *
     * @param id 测试模块ID
     * @return 结果
     */
    @Override
    public int deleteTestById(Long id) {
        return testMapper.deleteById(id);
    }

    /**
     * 导入test模块数据
     *
     * @param testList        用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importTest(List<Test> testList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(testList) || testList.size() == 0) {
            throw new BaseException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Test test : testList) {
            try {
                // 验证是否存在这个用户
                QueryWrapper<Test> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("name", test.getName());


                List<Test> listTest = testMapper.selectList(queryWrapper);
                if (listTest.size() < 1)//不存在就新增
                {

                    test.setCreateByName(operName);
                    this.insertTest(test);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + test.getName() + " 导入成功");
                } else if (isUpdateSupport) {//存在就覆盖修改
                    test.setUpdateBy(operName);
                    this.updateTest(test);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + test.getName() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + test.getName() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + test.getName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new BaseException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }


}
