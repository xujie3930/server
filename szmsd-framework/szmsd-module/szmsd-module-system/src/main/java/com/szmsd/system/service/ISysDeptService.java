package com.szmsd.system.service;

import com.szmsd.system.api.domain.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.system.domain.vo.TreeSelect;

import java.util.List;

/**
* <p>
    * 部门表 服务类
    * </p>
*
* @author lzw
* @since 2020-07-01
*/
public interface ISysDeptService extends IService<SysDept> {

        /**
        * 查询部门表模块
        *
        * @param id 部门表模块ID
        * @return 部门表模块
        */
        public SysDept selectSysDeptById(String id);

        /**
        * 查询部门表模块列表
        *
        * @param SysDept 部门表模块
        * @return 部门表模块集合
        */
        public List<SysDept> selectSysDeptList(SysDept sysDept);

        /**
        * 新增部门表模块
        *
        * @param SysDept 部门表模块
        * @return 结果
        */
        public int insertSysDept(SysDept sysDept);

        /**
        * 修改部门表模块
        *
        * @param SysDept 部门表模块
        * @return 结果
        */
        public int updateSysDept(SysDept sysDept);

        /**
        * 批量删除部门表模块
        *
        * @param ids 需要删除的部门表模块ID
        * @return 结果
        */
        public int deleteSysDeptByIds(List<String> ids);

        /**
        * 删除部门表模块信息
        *
        * @param id 部门表模块ID
        * @return 结果
        */
        public int deleteSysDeptById(String id);


        /**
         * 构建前端所需要下拉树结构
         *
         * @param sysDepts 机构列表
         * @return 下拉树结构列表
         */
        public List<TreeSelect> buildDeptTreeSelect(List<SysDept> sysDepts);

        /**
         * 构建前端所需要树结构
         *
         * @param sysDepts 网点列表
         * @return 树结构列表
         */
        public List<SysDept> buildDeptTree(List<SysDept> sysDepts);

    }

