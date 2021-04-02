package com.szmsd.pack.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.pack.domain.PackageManagement;
import com.szmsd.pack.dto.PackageMangAddDTO;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.mapper.PackageManagementMapper;
import com.szmsd.pack.service.IPackageMangServeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.pack.vo.PackageMangVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * package - 交货管理 - 地址信息表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
@Slf4j
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
        PackageManagement packageManagement = baseMapper.selectById(id);
        Optional.ofNullable(packageManagement).orElseThrow(() -> new BaseException("数据不存在"));
        return packageManagement;
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
    public int insertPackageManagement(PackageMangAddDTO packageManagement) {
        return baseMapper.insert(packageManagement.convertThis(PackageManagement.class));
    }

    /**
     * 修改package - 交货管理 - 地址信息表模块
     *
     * @param packageManagement package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    @Override
    public int updatePackageManagement(PackageMangAddDTO packageManagement) {
        PackageManagement updateInfo = packageManagement.convertThis(PackageManagement.class);
        return packageManagementMapper.update(updateInfo, Wrappers.<PackageManagement>lambdaUpdate()
                .eq(PackageManagement::getId, updateInfo.getId())
                .eq(PackageManagement::getExportType, 0)
                .set(PackageManagement::getDelFlag, 2)
                .set(PackageManagement::getSubmitTime, LocalDateTime.now()));

    }

    /**
     * 批量删除package - 交货管理 - 地址信息表模块
     *
     * @param ids 需要删除的package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    @Override
    public int deletePackageManagementByIds(List<String> ids) {
        return packageManagementMapper.update(new PackageManagement(), Wrappers.<PackageManagement>lambdaUpdate()
                .in(PackageManagement::getId, ids)
                .eq(PackageManagement::getExportType, 0)
                .set(PackageManagement::getDelFlag, 2)
                .set(PackageManagement::getSubmitTime, LocalDateTime.now())
        );
    }

    @Override
    public void setExportStatus(List<Integer> ids) {
        int update = packageManagementMapper.update(null, Wrappers.<PackageManagement>lambdaUpdate()
                .eq(PackageManagement::getDelFlag, 0)
                .eq(PackageManagement::getExportType, 0)
                .in(PackageManagement::getId, ids)
                .set(PackageManagement::getExportType, 1)
                .set(PackageManagement::getExportTime, LocalDateTime.now())
        );
        log.info("导出条数【{}】,修改状态条数【{}】", ids.size(), update);
    }
}

