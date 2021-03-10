package com.szmsd.http.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.http.domain.HtpTransaction;

import java.util.List;

/**
 * <p>
 * http事务处理表 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-10
 */
public interface IHtpTransactionService extends IService<HtpTransaction> {

    /**
     * 查询http事务处理表模块
     *
     * @param id http事务处理表模块ID
     * @return http事务处理表模块
     */
    HtpTransaction selectHtpTransactionById(String id);

    /**
     * 查询http事务处理表模块列表
     *
     * @param htpTransaction http事务处理表模块
     * @return http事务处理表模块集合
     */
    List<HtpTransaction> selectHtpTransactionList(HtpTransaction htpTransaction);

    /**
     * 新增http事务处理表模块
     *
     * @param htpTransaction http事务处理表模块
     * @return 结果
     */
    int insertHtpTransaction(HtpTransaction htpTransaction);

    /**
     * 修改http事务处理表模块
     *
     * @param htpTransaction http事务处理表模块
     * @return 结果
     */
    int updateHtpTransaction(HtpTransaction htpTransaction);

    /**
     * 批量删除http事务处理表模块
     *
     * @param ids 需要删除的http事务处理表模块ID
     * @return 结果
     */
    int deleteHtpTransactionByIds(List<String> ids);

    /**
     * 删除http事务处理表模块信息
     *
     * @param id http事务处理表模块ID
     * @return 结果
     */
    int deleteHtpTransactionById(String id);

}

