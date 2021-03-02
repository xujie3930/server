package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasVercodeMapper;
import com.szmsd.bas.domain.BasVercode;
import com.szmsd.bas.service.IBasVercodeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 短信发送表 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-09-10
 */
@Service
public class BasVercodeServiceImpl extends ServiceImpl<BasVercodeMapper, BasVercode> implements IBasVercodeService {


    /**
     * 查询短信发送表模块
     *
     * @param id 短信发送表模块ID
     * @return 短信发送表模块
     */
    @Override
    public BasVercode selectBasVercodeById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询短信发送表模块列表
     *
     * @param BasVercode 短信发送表模块
     * @return 短信发送表模块
     */
    @Override
    public List<BasVercode> selectBasVercodeList(BasVercode basVercode) {
        QueryWrapper<BasVercode> where = new QueryWrapper<BasVercode>();

        return baseMapper.selectList(where);
    }

    /**
     * 新增短信发送表模块
     *
     * @param BasVercode 短信发送表模块
     * @return 结果
     */
    @Override
    public int insertBasVercode(BasVercode basVercode) {
        return baseMapper.insert(basVercode);
    }

    /**
     * 修改短信发送表模块
     *
     * @param BasVercode 短信发送表模块
     * @return 结果
     */
    @Override
    public int updateBasVercode(BasVercode basVercode) {
        return baseMapper.updateById(basVercode);
    }

    /**
     * 批量删除短信发送表模块
     *
     * @param ids 需要删除的短信发送表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasVercodeByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除短信发送表模块信息
     *
     * @param id 短信发送表模块ID
     * @return 结果
     */
    @Override
    public int deleteBasVercodeById(String id) {
        return baseMapper.deleteById(id);
    }
}
