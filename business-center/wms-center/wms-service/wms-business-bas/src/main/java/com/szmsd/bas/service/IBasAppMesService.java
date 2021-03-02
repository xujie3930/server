package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasAppMes;

import java.util.List;

/**
 * <p>
 * App消息表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */
public interface IBasAppMesService extends IService<BasAppMes> {

    /**
     * 查询App消息表模块
     *
     * @param id App消息表模块ID
     * @return App消息表模块
     */
    public BasAppMes selectBasAppMesById(String id);

    /**
     * 查询App消息表模块列表
     *
     * @param BasAppMes App消息表模块
     * @return App消息表模块集合
     */
    public List<BasAppMes> selectBasAppMesList(BasAppMes basAppMes);

    /**
     * 新增App消息表模块
     *
     * @param BasAppMes App消息表模块
     * @return 结果
     */
    public int insertBasAppMes(BasAppMes basAppMes);

    /**
     * 修改App消息表模块
     *
     * @param BasAppMes App消息表模块
     * @return 结果
     */
    public int updateBasAppMes(BasAppMes basAppMes);

    /**
     * 批量删除App消息表模块
     *
     * @param ids 需要删除的App消息表模块ID
     * @return 结果
     */
    public int deleteBasAppMesByIds(List<String> ids);

    /**
     * 删除App消息表模块信息
     *
     * @param id App消息表模块ID
     * @return 结果
     */
    public int deleteBasAppMesById(String id);

    int deleteBySourceId(String sourceId);

    List<BasAppMes> getPushAppMsgList();

    boolean updateBasAppMesList(List<BasAppMes> basAppMesList);
}

