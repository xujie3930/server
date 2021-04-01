package com.szmsd.pack.service.impl;

import com.szmsd.pack.domain.PackageManagement;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.mapper.PackageManagementMapper;
import com.szmsd.pack.service.IPackageMangServeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.pack.vo.PackageMangVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * package - 交货管理 - 地址信息表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
@Service
public class PackageMangServeServiceImpl extends ServiceImpl<PackageManagementMapper, PackageManagement> implements IPackageMangServeService {

    @Resource
    private PackageManagementMapper packageManagementMapper;

    /**
     * 查询package - 交货管理 - 地址信息表模块
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return package - 交货管理 - 地址信息表模块
     */
    @Override
    public PackageManagement selectPackageManagementById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<PackageMangVO> selectPackageManagementList(PackageMangQueryDTO packageManagement) {
        return packageManagementMapper.selectPackageManagementList(packageManagement);
    }


    /**
     * 新增package - 交货管理 - 地址信息表模块
     *
     * @param packageManagement package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    @Override
    public int insertPackageManagement(PackageManagement packageManagement) {
        return baseMapper.insert(packageManagement);
    }

    /**
     * 修改package - 交货管理 - 地址信息表模块
     *
     * @param packageManagement package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    @Override
    public int updatePackageManagement(PackageManagement packageManagement) {
        return baseMapper.updateById(packageManagement);
    }

    /**
     * 批量删除package - 交货管理 - 地址信息表模块
     *
     * @param ids 需要删除的package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    @Override
    public int deletePackageManagementByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块信息
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    @Override
    public int deletePackageManagementById(String id) {
        return baseMapper.deleteById(id);
    }


}

