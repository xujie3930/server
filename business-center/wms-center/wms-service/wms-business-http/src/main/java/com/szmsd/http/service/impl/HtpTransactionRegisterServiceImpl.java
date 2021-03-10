package com.szmsd.http.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.http.domain.HtpTransactionRegister;
import com.szmsd.http.mapper.HtpTransactionRegisterMapper;
import com.szmsd.http.service.IHtpTransactionRegisterService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * http事务注册表 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-10
 */
@Service
public class HtpTransactionRegisterServiceImpl extends ServiceImpl<HtpTransactionRegisterMapper, HtpTransactionRegister> implements IHtpTransactionRegisterService {


    /**
     * 查询http事务注册表模块
     *
     * @param id http事务注册表模块ID
     * @return http事务注册表模块
     */
    @Override
    public HtpTransactionRegister selectHtpTransactionRegisterById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询http事务注册表模块列表
     *
     * @param htpTransactionRegister http事务注册表模块
     * @return http事务注册表模块
     */
    @Override
    public List<HtpTransactionRegister> selectHtpTransactionRegisterList(HtpTransactionRegister htpTransactionRegister) {
        QueryWrapper<HtpTransactionRegister> where = new QueryWrapper<HtpTransactionRegister>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增http事务注册表模块
     *
     * @param htpTransactionRegister http事务注册表模块
     * @return 结果
     */
    @Override
    public int insertHtpTransactionRegister(HtpTransactionRegister htpTransactionRegister) {
        return baseMapper.insert(htpTransactionRegister);
    }

    /**
     * 修改http事务注册表模块
     *
     * @param htpTransactionRegister http事务注册表模块
     * @return 结果
     */
    @Override
    public int updateHtpTransactionRegister(HtpTransactionRegister htpTransactionRegister) {
        return baseMapper.updateById(htpTransactionRegister);
    }

    /**
     * 批量删除http事务注册表模块
     *
     * @param ids 需要删除的http事务注册表模块ID
     * @return 结果
     */
    @Override
    public int deleteHtpTransactionRegisterByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除http事务注册表模块信息
     *
     * @param id http事务注册表模块ID
     * @return 结果
     */
    @Override
    public int deleteHtpTransactionRegisterById(String id) {
        return baseMapper.deleteById(id);
    }


}

