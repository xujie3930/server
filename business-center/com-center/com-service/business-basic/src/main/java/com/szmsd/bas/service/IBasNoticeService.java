package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasNotice;

import java.util.List;

/**
 * <p>
 * 公告通知明细 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */
public interface IBasNoticeService extends IService<BasNotice> {

    /**
     * 查询公告通知明细模块
     *
     * @param id 公告通知明细模块ID
     * @return 公告通知明细模块
     */
    public BasNotice selectBasNoticeById(String id);

    /**
     * 查询公告通知明细模块列表
     *
     * @param BasNotice 公告通知明细模块
     * @return 公告通知明细模块集合
     */
    public List<BasNotice> selectBasNoticeList(BasNotice basNotice);

    /**
     * 新增公告通知明细模块
     *
     * @param BasNotice 公告通知明细模块
     * @return 结果
     */
    public int insertBasNotice(BasNotice basNotice);

    /**
     * 修改公告通知明细模块
     *
     * @param BasNotice 公告通知明细模块
     * @return 结果
     */
    public int updateBasNotice(BasNotice basNotice);

    /**
     * 批量删除公告通知明细模块
     *
     * @param ids 需要删除的公告通知明细模块ID
     * @return 结果
     */
    public int deleteBasNoticeByIds(List<String> ids);

    /**
     * 删除公告通知明细模块信息
     *
     * @param id 公告通知明细模块ID
     * @return 结果
     */
    public int deleteBasNoticeById(String id);
}
