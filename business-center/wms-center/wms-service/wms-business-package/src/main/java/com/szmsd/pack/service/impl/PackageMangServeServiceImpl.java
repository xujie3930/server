package com.szmsd.pack.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.datascope.service.AwaitUserService;
import com.szmsd.pack.api.feign.client.IBasFeignClientService;
import com.szmsd.pack.constant.PackageConstant;
import com.szmsd.pack.domain.PackageManagement;
import com.szmsd.pack.dto.PackageMangAddDTO;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.mapper.PackageManagementMapper;
import com.szmsd.pack.service.IPackageMangServeService;
import com.szmsd.pack.vo.PackageMangVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Resource
    private BasFeignService basFeignService;

    @Resource
    private AwaitUserService awaitUserService;

    @Resource
    private IBasFeignClientService iBasFeignClientService;


    /**
     * 获取用户sellerCode
     *
     * @return
     */
    private String getSellCode() {
        //UserInfo info = awaitUserService.info();
        String loginSellerCode = iBasFeignClientService.getLoginSellerCode();
        return Optional.ofNullable(loginSellerCode).orElse("");
    }


    /**
     * 单号生成
     *
     * @return
     */
    public String genNo() {
        String code = PackageConstant.GENERATE_CODE;
        String appId = PackageConstant.GENERATE_APP_ID;
        log.info("调用自动生成单号：code={}", code);
        R<List<String>> r = basFeignService.create(new BasCodeDto().setAppId(appId).setCode(code));
        AssertUtil.notNull(r, "单号生成失败");
        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, code + "单号生成失败：" + r.getMsg());
        String s = r.getData().get(0);
        s = PackageConstant.LS_PREFIX + getSellCode() + s;
        log.info("调用自动生成单号：调用完成, {}-{}", code, s);
        return s;
    }


    /**
     * 查询package - 交货管理 - 地址信息表模块
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return package - 交货管理 - 地址信息表模块
     */
    @Override
    public PackageMangVO selectPackageManagementById(String id) {
        PackageManagement packageManagement = baseMapper.selectById(id);
        Optional.ofNullable(packageManagement).orElseThrow(() -> new BaseException("数据不存在"));
        PackageMangVO packageMangVO = packageManagement.convertThis(PackageMangVO.class);
        packageMangVO.setShowChoose();
        return packageMangVO;
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
        packageManagement.setOrderNo(genNo());
        packageManagement.setSellerCode(getSellCode());
        return baseMapper.insert(packageManagement.convertThis(PackageManagement.class));
    }

    /**
     * 修改package - 交货管理 - 地址信息表模块
     *
     * @param packageManagement package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updatePackageManagement(PackageMangAddDTO packageManagement) {
        AssertUtil.isTrue(null != packageManagement.getId(), "请选中修改的数据！");
        PackageManagement updateInfo = packageManagement.convertThis(PackageManagement.class);
        int update = packageManagementMapper.update(updateInfo, Wrappers.<PackageManagement>lambdaUpdate()
                .eq(PackageManagement::getId, updateInfo.getId())
                .eq(PackageManagement::getExportType, 0)
                .set(PackageManagement::getSubmitTime, LocalDateTime.now()));
        if (update != 1) {
            throw new BaseException("已经导出暂不支持修改");
        }
        return update;

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

