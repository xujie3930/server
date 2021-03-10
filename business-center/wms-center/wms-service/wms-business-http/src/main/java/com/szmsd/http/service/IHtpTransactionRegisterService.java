package com.szmsd.http.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.http.domain.HtpTransactionRegister;

import java.util.List;

/**
 * <p>
 * http事务注册表 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-10
 */
public interface IHtpTransactionRegisterService extends IService<HtpTransactionRegister> {

    /**
     * 查询http事务注册表模块
     *
     * @param id http事务注册表模块ID
     * @return http事务注册表模块
     */
    HtpTransactionRegister selectHtpTransactionRegisterById(String id);

    /**
     * 查询http事务注册表模块列表
     *
     * @param htpTransactionRegister http事务注册表模块
     * @return http事务注册表模块集合
     */
    List<HtpTransactionRegister> selectHtpTransactionRegisterList(HtpTransactionRegister htpTransactionRegister);

    /**
     * 新增http事务注册表模块
     *
     * @param htpTransactionRegister http事务注册表模块
     * @return 结果
     */
    int insertHtpTransactionRegister(HtpTransactionRegister htpTransactionRegister);

    /**
     * 修改http事务注册表模块
     *
     * @param htpTransactionRegister http事务注册表模块
     * @return 结果
     */
    int updateHtpTransactionRegister(HtpTransactionRegister htpTransactionRegister);

    /**
     * 批量删除http事务注册表模块
     *
     * @param ids 需要删除的http事务注册表模块ID
     * @return 结果
     */
    int deleteHtpTransactionRegisterByIds(List<String> ids);

    /**
     * 删除http事务注册表模块信息
     *
     * @param id http事务注册表模块ID
     * @return 结果
     */
    int deleteHtpTransactionRegisterById(String id);

}

