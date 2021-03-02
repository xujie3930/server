package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasCuspriceCode;

import java.util.List;

/**
 * <p>
 * 客户报价子表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-09-21
 */
public interface IBasCuspriceCodeService extends IService<BasCuspriceCode> {

    /**
     * 查询客户报价子表模块
     *
     * @param id 客户报价子表模块ID
     * @return 客户报价子表模块
     */
    public BasCuspriceCode selectBasCuspriceCodeById(String id);

    /**
     * 查询客户报价子表模块列表
     *
     * @param BasCuspriceCode 客户报价子表模块
     * @return 客户报价子表模块集合
     */
    public List<BasCuspriceCode> selectBasCuspriceCodeList(BasCuspriceCode basCuspriceCode);

    /**
     * 新增客户报价子表模块
     *
     * @param BasCuspriceCode 客户报价子表模块
     * @return 结果
     */
    public int insertBasCuspriceCode(BasCuspriceCode basCuspriceCode);

    /**
     * 修改客户报价子表模块
     *
     * @param BasCuspriceCode 客户报价子表模块
     * @return 结果
     */
    public int updateBasCuspriceCode(BasCuspriceCode basCuspriceCode);

    /**
     * 批量删除客户报价子表模块
     *
     * @param ids 需要删除的客户报价子表模块ID
     * @return 结果
     */
    public int deleteBasCuspriceCodeByIds(List<String> ids);

    /**
     * 删除客户报价子表模块信息
     *
     * @param id 客户报价子表模块ID
     * @return 结果
     */
    public int deleteBasCuspriceCodeById(String id);

    /**
     * 根据 报价id 删除
     * @param cuspriceId
     * @return
     */
    int deleteByCusId(String cuspriceId);
}
