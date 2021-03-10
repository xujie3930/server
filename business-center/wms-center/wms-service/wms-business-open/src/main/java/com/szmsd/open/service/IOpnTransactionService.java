package com.szmsd.open.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.open.domain.OpnTransaction;

import java.util.List;

/**
 * <p>
 * 业务处理 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-08
 */
public interface IOpnTransactionService extends IService<OpnTransaction> {

    /**
     * 查询业务处理模块
     *
     * @param id 业务处理模块ID
     * @return 业务处理模块
     */
    OpnTransaction selectOpnTransactionById(String id);

    /**
     * 查询业务处理模块列表
     *
     * @param opnTransaction 业务处理模块
     * @return 业务处理模块集合
     */
    List<OpnTransaction> selectOpnTransactionList(OpnTransaction opnTransaction);

    /**
     * 新增业务处理模块
     *
     * @param opnTransaction 业务处理模块
     * @return 结果
     */
    int insertOpnTransaction(OpnTransaction opnTransaction);

    /**
     * 修改业务处理模块
     *
     * @param opnTransaction 业务处理模块
     * @return 结果
     */
    int updateOpnTransaction(OpnTransaction opnTransaction);

    /**
     * 批量删除业务处理模块
     *
     * @param ids 需要删除的业务处理模块ID
     * @return 结果
     */
    int deleteOpnTransactionByIds(List<String> ids);

    /**
     * 删除业务处理模块信息
     *
     * @param id 业务处理模块ID
     * @return 结果
     */
    int deleteOpnTransactionById(String id);

    /**
     * 判断有没有rep
     *
     * @param requestUri    requestUri
     * @param transactionId transactionId
     * @return boolean
     */
    boolean hasRep(String requestUri, String transactionId);

    /**
     * 新增记录
     *
     * @param traceId       traceId
     * @param requestUri    requestUri
     * @param transactionId transactionId
     */
    void add(String traceId, String requestUri, String transactionId);

    /**
     * rep
     *
     * @param traceId traceId
     */
    void onRep(String traceId);
}

