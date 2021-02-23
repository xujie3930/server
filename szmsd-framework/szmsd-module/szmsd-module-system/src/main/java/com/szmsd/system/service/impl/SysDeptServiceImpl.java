package com.szmsd.system.service.impl;

import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.system.api.domain.SysDept;
import com.szmsd.system.domain.vo.TreeSelect;
import com.szmsd.system.mapper.SysDeptMapper;
import com.szmsd.system.service.ISysDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
* <p>
    * 部门表 服务实现类
    * </p>
*
* @author lzw
* @since 2020-07-01
*/
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {


        /**
        * 查询部门表模块
        *
        * @param id 部门表模块ID
        * @return 部门表模块
        */
        @Override
        public SysDept selectSysDeptById(String id)
        {
        return baseMapper.selectById(id);
        }

        /**
        * 查询部门表模块列表
        *
        * @param sysDept 部门表模块
        * @return 部门表模块
        */
        @Override
        public List<SysDept> selectSysDeptList(SysDept sysDept)
        {
            int  c= 1/0;
        QueryWrapper<SysDept> where = new QueryWrapper<SysDept>();

        return baseMapper.selectList(where);
        }

        /**
        * 新增部门表模块
        *
        * @param sysDept 部门表模块
        * @return 结果
        */
        @Override
        public int insertSysDept(SysDept sysDept)
        {
        return baseMapper.insert(sysDept);
        }

        /**
        * 修改部门表模块
        *
        * @param sysDept 部门表模块
        * @return 结果
        */
        @Override
        public int updateSysDept(SysDept sysDept)
        {
        return baseMapper.updateById(sysDept);
        }

        /**
        * 批量删除部门表模块
        *
        * @param ids 需要删除的部门表模块ID
        * @return 结果
        */
        @Override
        public int deleteSysDeptByIds(List<String>  ids)
       {
            return baseMapper.deleteBatchIds(ids);
       }

        /**
        * 删除部门表模块信息
        *
        * @param id 部门表模块ID
        * @return 结果
        */
        @Override
        public int deleteSysDeptById(String id)
        {
        return baseMapper.deleteById(id);
        }

    /**
     * 构建前端所需要树结构
     *
     * @param sysDepts 机构列表
     * @return 树结构列表
     */
    @Override
    public List<SysDept> buildDeptTree(List<SysDept> sysDepts)
    {
        List<SysDept> returnList = new ArrayList<SysDept>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysDept dept : sysDepts)
        {
            tempList.add(dept.getId());
        }
        for (Iterator<SysDept> iterator = sysDepts.iterator(); iterator.hasNext();)
        {
            SysDept sysDept = (SysDept) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(sysDept.getParentId()))
            {
                recursionFn(sysDepts, sysDept);
                returnList.add(sysDept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = sysDepts;
        }
        return returnList;
    }


    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t)
    {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                // 判断是否有子节点
                Iterator<SysDept> it = childList.iterator();
                while (it.hasNext())
                {
                    SysDept n = (SysDept) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t)
    {
        List<SysDept> tlist = new ArrayList<SysDept>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext())
        {
            SysDept n = (SysDept) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }
    
    /**
     * 构建前端所需要下拉树结构
     *
     * @param sysDepts 机构列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDept> sysDepts)
    {
        List<SysDept> deptTrees = buildDeptTree(sysDepts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t)
    {
        return getChildList(list, t).size() > 0 ? true : false;
    }
}

