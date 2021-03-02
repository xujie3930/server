package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasE3Mes;

import java.util.List;

/**
 * <p>
 * E3消息表 服务类
 * </p>
 *
 * @author admin
 * @since 2020-11-28
 */
public interface IBasE3MesService extends IService<BasE3Mes> {

    /**
     * 查询E3消息表模块
     *
     * @param id E3消息表模块ID
     * @return E3消息表模块
     */
    BasE3Mes selectBasE3MesById(String id);

    /**
     * 查询E3消息表模块列表
     *
     * @param basE3Mes E3消息表模块
     * @return E3消息表模块集合
     */
    List<BasE3Mes> selectBasE3MesList(BasE3Mes basE3Mes);

    /**
     * 新增E3消息表模块
     *
     * @param basE3Mes E3消息表模块
     * @return 结果
     */
    int insertBasE3Mes(BasE3Mes basE3Mes);

    /**
     * 修改E3消息表模块
     *
     * @param basE3Mes E3消息表模块
     * @return 结果
     */
    int updateBasE3Mes(BasE3Mes basE3Mes);

    /**
     * 批量删除E3消息表模块
     *
     * @param idList 需要删除的E3消息表模块ID
     * @return 结果
     */
    int batchDel(List<String> idList);

    int deleteBySourceId(String sourceId);
}

