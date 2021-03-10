package com.szmsd.http.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.http.domain.HtpTransaction;
import com.szmsd.http.mapper.HtpTransactionMapper;
import com.szmsd.http.service.IHtpTransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * http事务处理表 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-10
 */
@Service
public class HtpTransactionServiceImpl extends ServiceImpl<HtpTransactionMapper, HtpTransaction> implements IHtpTransactionService {


    /**
     * 查询http事务处理表模块
     *
     * @param id http事务处理表模块ID
     * @return http事务处理表模块
     */
    @Override
    public HtpTransaction selectHtpTransactionById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询http事务处理表模块列表
     *
     * @param htpTransaction http事务处理表模块
     * @return http事务处理表模块
     */
    @Override
    public List<HtpTransaction> selectHtpTransactionList(HtpTransaction htpTransaction) {
        QueryWrapper<HtpTransaction> where = new QueryWrapper<HtpTransaction>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增http事务处理表模块
     *
     * @param htpTransaction http事务处理表模块
     * @return 结果
     */
    @Override
    public int insertHtpTransaction(HtpTransaction htpTransaction) {
        return baseMapper.insert(htpTransaction);
    }

    /**
     * 修改http事务处理表模块
     *
     * @param htpTransaction http事务处理表模块
     * @return 结果
     */
    @Override
    public int updateHtpTransaction(HtpTransaction htpTransaction) {
        return baseMapper.updateById(htpTransaction);
    }

    /**
     * 批量删除http事务处理表模块
     *
     * @param ids 需要删除的http事务处理表模块ID
     * @return 结果
     */
    @Override
    public int deleteHtpTransactionByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除http事务处理表模块信息
     *
     * @param id http事务处理表模块ID
     * @return 结果
     */
    @Override
    public int deleteHtpTransactionById(String id) {
        return baseMapper.deleteById(id);
    }


}

