package com.szmsd.system.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.system.domain.SysDeptPost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 机构部门与岗位关联表 服务类
 * </p>
 *
 * @author lzw
 * @since 2020-07-06
 */
public interface ISysDeptPostService extends IService<SysDeptPost> {

    /**
     * 查询机构部门与岗位关联表模块
     *
     * @param id 机构部门与岗位关联表模块ID
     * @return 机构部门与岗位关联表模块
     */
    public SysDeptPost selectSysDeptPostById(String id);

    /**
     * 查询机构部门与岗位关联表模块列表
     *
     * @param SysDeptPost 机构部门与岗位关联表模块
     * @return 机构部门与岗位关联表模块集合
     */
    public List<SysDeptPost> selectSysDeptPostList(SysDeptPost sysDeptPost);

    /**
     * 新增机构部门与岗位关联表模块
     *
     * @param SysDeptPost 机构部门与岗位关联表模块
     * @return 结果
     */
    public R insertSysDeptPost(SysDeptPost sysDeptPost);

    /**
     * 修改机构部门与岗位关联表模块
     *
     * @param SysDeptPost 机构部门与岗位关联表模块
     * @return 结果
     */
    public R updateSysDeptPost(SysDeptPost sysDeptPost);


    /**
     * 删除部门 岗位关系
     *
     * @param sysDeptPost
     * @return
     */
    public int deleteSysDeptPostByDeptPost(SysDeptPost sysDeptPost);
}

