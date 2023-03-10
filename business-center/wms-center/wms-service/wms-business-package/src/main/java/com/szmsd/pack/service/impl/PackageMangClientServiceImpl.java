package com.szmsd.pack.service.impl;


import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.datascope.service.AwaitUserService;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.pack.domain.PackageAddress;
import com.szmsd.pack.domain.PackageManagementConfig;
import com.szmsd.pack.domain.PackageManagementConfigWeek;
import com.szmsd.pack.dto.PackageAddressAddDTO;
import com.szmsd.pack.dto.PackageMangAddDTO;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.mapper.PackageAddressMapper;
import com.szmsd.pack.mapper.PackageManagementConfigMapper;
import com.szmsd.pack.mapper.PackageManagementConfigWeekMapper;
import com.szmsd.pack.service.IPackageMangClientService;
import com.szmsd.pack.service.IPackageMangServeService;
import com.szmsd.pack.vo.PackageAddressVO;
import com.szmsd.pack.vo.PackageManagementConfigVo;
import com.szmsd.pack.vo.PackageMangVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * <p>
 * package - 交货管理 - 地址信息表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
@Service
public class PackageMangClientServiceImpl extends ServiceImpl<PackageAddressMapper, PackageAddress> implements IPackageMangClientService {

    @Resource
    private AwaitUserService awaitUserService;

    @Resource
    private IPackageMangServeService packageManagementService;
    @Autowired
    private PackageManagementConfigMapper packageManagementConfigMapper;

    @Autowired
    private PackageManagementConfigWeekMapper packageManagementConfigWeekMapper;
    /**
     * 获取用户sellerCode
     *
     * @return
     */
    private String getSellCode() {
        return Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
    }

    /**
     * 查询package - 交货管理 - 地址信息表模块
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return package - 交货管理 - 地址信息表模块
     */
    @Override
    public PackageAddressVO selectPackageAddressById(String id) {
        PackageAddress packageAddress = baseMapper.selectById(id);
        Optional.ofNullable(packageAddress).orElseThrow(() -> new BaseException("数据不存在！"));
        PackageAddressVO packageAddressVO = packageAddress.convertThis(PackageAddressVO.class);
        packageAddressVO.setShowAddr();
        return packageAddressVO;
    }

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     *
     * @param packageAddress package - 交货管理 - 地址信息表模块
     * @return package - 交货管理 - 地址信息表模块
     */
    @Override
    public List<PackageAddressVO> selectPackageAddressList(PackageMangQueryDTO packageAddress) {
//        packageAddress.setSellerCode(getSellCode());
        // 子母单的查询 如果没有传值就只能才自己的
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (StringUtils.isEmpty(packageAddress.getCustomCode())) {
                packageAddress.setCustomCode(cusCode);
            }
        }
        List<PackageAddressVO> packageAddressVoList = baseMapper.selectPackageAddressList(packageAddress);
        packageAddressVoList.forEach(PackageAddressVO::setShowAddr);
        return packageAddressVoList;
    }

    /**
     * 新增package - 交货管理 - 地址信息表模块
     *
     * @param packageAddress package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPackageAddress(PackageAddressAddDTO packageAddress) {
        packageAddress.setSellerCode(getSellCode());
        offDefaultAddr(packageAddress);
        //第一次新增 默认 设置为默认地址
        Integer integer = baseMapper.selectCount(Wrappers.<PackageAddress>lambdaQuery()
                .eq(PackageAddress::getSellerCode, getSellCode())
                .eq(PackageAddress::getDelFlag, 0)
                .eq(PackageAddress::getDefaultFlag, 1)
        );
        if (integer == 0) {
            packageAddress.setDefaultFlag(1);
        }
        return baseMapper.insert(packageAddress.convertThis(PackageAddress.class));
    }

    /**
     * 关闭 不为当前id 其他默认地址设置
     *
     * @param packageAddress
     */
    @Transactional(rollbackFor = Exception.class)
    void offDefaultAddr(PackageAddressAddDTO packageAddress) {
        if (null != packageAddress.getDefaultFlag() && packageAddress.getDefaultFlag() == 1) {
            baseMapper.update(null, Wrappers.<PackageAddress>lambdaUpdate()
                    .eq(PackageAddress::getDelFlag, 0)
                    .eq(PackageAddress::getDefaultFlag, 1)
                    .eq(PackageAddress::getSellerCode, packageAddress.getSellerCode())
                    .ne(packageAddress.getId() != null, PackageAddress::getId, packageAddress.getId())
                    .set(PackageAddress::getDefaultFlag, 0)
            );
        }
    }

    /**
     * 修改package - 交货管理 - 地址信息表模块
     *
     * @param packageAddress package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePackageAddress(PackageAddressAddDTO packageAddress) {
        AssertUtil.isTrue(packageAddress.getId() != null && packageAddress.getId() > 0, "更新数据不存在");
        packageAddress.setSellerCode(getSellCode());
        offDefaultAddr(packageAddress);
        return baseMapper.updateById(packageAddress.convertThis(PackageAddress.class));
    }

    /**
     * 批量删除package - 交货管理 - 地址信息表模块
     *
     * @param ids 需要删除的package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePackageAddressByIds(List<String> ids) {
        setOtherDefaultAddr(ids);
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 当删除的ids中存在默认地址，设置其他地址为 默认地址
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    void setOtherDefaultAddr(List<String> ids) {
        baseMapper.update(null, Wrappers.<PackageAddress>lambdaUpdate()
                .eq(PackageAddress::getSellerCode, getSellCode())
                .notIn(CollectionUtils.isNotEmpty(ids), PackageAddress::getId, ids)
                .set(PackageAddress::getDefaultFlag, 1)
                .orderByDesc(PackageAddress::getDefaultFlag)
                .last("LIMIT 1")
        );
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块信息
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePackageAddressById(String id) {
        setOtherDefaultAddr(Collections.singletonList(id));
        return baseMapper.deleteById(id);
    }

    /**
     * 设置默认地址
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setDefaultAddr(String id) {
        offDefaultAddr(new PackageAddressAddDTO().setId(Integer.parseInt(id)).setDefaultFlag(1).setSellerCode(getSellCode()));
        //设置当前id默认
        return baseMapper.update(new PackageAddress(), Wrappers.<PackageAddress>lambdaUpdate()
                .eq(PackageAddress::getId, id)
                .set(PackageAddress::getDefaultFlag, 1)
                .last("LIMIT 1")
        );
    }

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     */
    @Override
    public List<PackageMangVO> selectPackageManagementList(PackageMangQueryDTO packageMangQueryDTO) {
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (StringUtils.isEmpty(packageMangQueryDTO.getCustomCode())) {
                packageMangQueryDTO.setCustomCode(cusCode);
            }
        }
        return packageManagementService.selectPackageManagementList(packageMangQueryDTO);
    }

    @Override
    public PackageMangVO selectPackageManagementById(String id) {
        return packageManagementService.selectPackageManagementById(id);
    }

    @Override
    public int insertPackageManagement(PackageMangAddDTO packageManagement) {
        return packageManagementService.insertPackageManagement(packageManagement);
    }

    @Override
    public int updatePackageManagement(PackageMangAddDTO packageManagement) {
        return packageManagementService.updatePackageManagement(packageManagement);
    }

    @Override
    public int deletePackageManagementByIds(List<String> ids) {
        return packageManagementService.deletePackageManagementByIds(ids);
    }

    @Override
    public int addPackageConfig(PackageManagementConfig packageManagementConfig) {
        int a=0;
        List<PackageManagementConfig> packageManagementConfig1=packageManagementConfigMapper.selectPckageManagementConfigus(packageManagementConfig);
        if (packageManagementConfig1.size()==0){


        a= packageManagementConfigMapper.insertSelective(packageManagementConfig);
        List<PackageManagementConfigWeek> list=packageManagementConfig.getPackageManagementConfigWeekList();
        if (list.size()>0) {
            list.forEach(x -> {
                x.setPackageManagementConfigId(packageManagementConfig.getId());
                packageManagementConfigWeekMapper.insertSelective(x);
            });
        }

        }
        return a;

    }



    @Override
    public int editPackageConfig(PackageManagementConfig packageManagementConfig) {
        int a=0;
        List<PackageManagementConfig> packageManagementConfig1=packageManagementConfigMapper.selectPckageManagementConfigus(packageManagementConfig);
        if (packageManagementConfig1.size()==0) {
            a = packageManagementConfigMapper.updateByPrimaryKeySelective(packageManagementConfig);
            List<PackageManagementConfigWeek> list = packageManagementConfig.getPackageManagementConfigWeekList();
            packageManagementConfigWeekMapper.deleteByPrimaryKey(packageManagementConfig.getId());
            list.forEach(x -> {
                x.setPackageManagementConfigId(packageManagementConfig.getId());
                packageManagementConfigWeekMapper.insertSelective(x);
            });
        }
        return a;
    }

    @Override
    public List<PackageManagementConfigVo> selectpackageConfigList(PackageMangQueryDTO packageMangQueryDTO) {
        String sellerCode = packageMangQueryDTO.getSellerCode();

        if (StringUtils.isNotEmpty(sellerCode)) {
            try {
                sellerCode = URLDecoder.decode(packageMangQueryDTO.getSellerCode(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            List<String> sellerCodeList = this.splitToArray(sellerCode, "[\n,]");

            packageMangQueryDTO.setSellerCodeList(sellerCodeList);

        }
        List<PackageManagementConfigVo> list = packageManagementConfigMapper.selectByPrimaryKey(packageMangQueryDTO);

        return list;
    }

    @Override
    public int deletePackageConfig(PackageMangQueryDTO packageMangQueryDTO) {
         if (packageMangQueryDTO.getIds().size()>0){
             packageMangQueryDTO.getIds().forEach(x->{
                 packageManagementConfigMapper.deleteByPrimaryKey(x);
                 packageManagementConfigWeekMapper.deleteByPrimaryKey(x);
             });

         }
        return 1;
    }

    @Override
    public PackageManagementConfig packageConfigBy(PackageMangQueryDTO packageMangQueryDTO) {
      return   packageManagementConfigMapper.packageConfigBy(packageMangQueryDTO.getIdu());
    }

    public static List<String> splitToArray(String text, String split) {
        String[] arr = text.split(split);
        if (arr.length == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(s)) {
                continue;
            }
            list.add(s);
        }
        return list;
    }


}

