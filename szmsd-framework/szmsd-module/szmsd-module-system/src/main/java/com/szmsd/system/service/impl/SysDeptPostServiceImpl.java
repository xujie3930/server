package com.szmsd.system.service.impl;

import com.szmsd.system.domain.SysDeptPost;
import com.szmsd.system.enums.ExceptionEnum;
import com.szmsd.system.mapper.SysDeptPostMapper;
import com.szmsd.system.service.ISysDeptPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.NULL;

/**
 * <p>
 * 机构部门与岗位关联表 服务实现类
 * </p>
 *
 * @author lzw
 * @since 2020-07-06
 */
@Service
public class SysDeptPostServiceImpl extends ServiceImpl<SysDeptPostMapper, SysDeptPost> implements ISysDeptPostService {


    /**
     * 查询机构部门与岗位关联表模块
     *
     * @param id 机构部门与岗位关联表模块ID
     * @return 机构部门与岗位关联表模块
     */
    @Override
    public SysDeptPost selectSysDeptPostById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询机构部门与岗位关联表模块列表
     *
     * @param SysDeptPost 机构部门与岗位关联表模块
     * @return 机构部门与岗位关联表模块
     */
    @Override
    public List<SysDeptPost> selectSysDeptPostList(SysDeptPost sysDeptPost) {
        QueryWrapper<SysDeptPost> where = new QueryWrapper<SysDeptPost>();
        where.setEntity(sysDeptPost);
        return baseMapper.selectList(where);
    }

    /**
     * 新增机构部门与岗位关联表模块
     *
     * @param SysDeptPost 机构部门与岗位关联表模块
     * @return 结果
     */
    @Override
    public R insertSysDeptPost(SysDeptPost sysDeptPost) {
        QueryWrapper<SysDeptPost> where = new QueryWrapper<SysDeptPost>();
        SysDeptPost sysDeptPost1 = new SysDeptPost();
        sysDeptPost1.setDeptCode(sysDeptPost.getDeptCode());
        sysDeptPost1.setPostCode(sysDeptPost.getPostCode());
        where.setEntity(sysDeptPost1);
        int count = baseMapper.selectCount(where);
        if (count > 0) {
            return R.failed(ExceptionEnum.DEPT_AND_POST_EXISTING.getMessage());
        }
        return baseMapper.insert(sysDeptPost) > 0 ? R.ok() : R.failed();
    }

    /**
     * 修改机构部门与岗位关联表模块
     *
     * @param SysDeptPost 机构部门与岗位关联表模块
     * @return 结果
     */
    @Override
    public R updateSysDeptPost(SysDeptPost sysDeptPost) {
        QueryWrapper<SysDeptPost> where = new QueryWrapper<SysDeptPost>();
        SysDeptPost sysDeptPost1 = new SysDeptPost();
        sysDeptPost1.setDeptCode(sysDeptPost.getDeptCode());
        sysDeptPost1.setPostCode(sysDeptPost.getPostCode());
        where.setEntity(sysDeptPost1);
        int count = baseMapper.selectCount(where);
        if (count > 0) {
            return R.failed(ExceptionEnum.DEPT_AND_POST_EXISTING.getMessage());
        }
        return baseMapper.updateById(sysDeptPost) > 0 ? R.ok() : R.failed();
    }


    /**
     * 删除机构部门与岗位关联表模块
     *
     * @param SysDeptPost 机构部门与岗位关联表模块
     * @return 结果
     */
    @Override
    public int deleteSysDeptPostByDeptPost(SysDeptPost sysDeptPost) {
        Map map = new HashMap<>();
        map.put("dept_code", sysDeptPost.getDeptCode());
        map.put("post_code", sysDeptPost.getPostCode());
        return baseMapper.deleteByMap(map);
    }

}

