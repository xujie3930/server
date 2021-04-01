package com.szmsd.pack.service.impl;


import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.pack.domain.PackageAddress;
import com.szmsd.pack.dto.PackageAddressAddDTO;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.mapper.PackageAddressMapper;
import com.szmsd.pack.service.IPackageAddressService;
import com.szmsd.pack.vo.PackageAddressVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
public class PackageAddressServiceImpl extends ServiceImpl<PackageAddressMapper, PackageAddress> implements IPackageAddressService {


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
        return packageAddress.convertThis(PackageAddressVO.class);
    }

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     *
     * @param packageAddress package - 交货管理 - 地址信息表模块
     * @return package - 交货管理 - 地址信息表模块
     */
    @Override
    public List<PackageAddressVO> selectPackageAddressList(PackageMangQueryDTO packageAddress) {
        baseMapper.selectPackageAddressList(packageAddress).forEach(
                x -> x.setShowAddr(String.join(",", x.getProvinceNameZh(), x.getCityNameZh(), x.getDistrictNameZh()))
        );
        return baseMapper.selectPackageAddressList(packageAddress);
    }

    /**
     * 新增package - 交货管理 - 地址信息表模块
     *
     * @param packageAddress package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    @Override
    public int insertPackageAddress(PackageAddressAddDTO packageAddress) {
        changeDefaultAddr(packageAddress);
        return baseMapper.insert(packageAddress.convertThis(PackageAddress.class));
    }

    /**
     * 修改默认地址
     *
     * @param packageAddress
     */
    private void changeDefaultAddr(PackageAddressAddDTO packageAddress) {
        if (packageAddress.getDefaultFlag() == 1) {
            baseMapper.update(null, Wrappers.<PackageAddress>lambdaUpdate()
                    .eq(PackageAddress::getDelFlag, 0)
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
    public int updatePackageAddress(PackageAddressAddDTO packageAddress) {
        AssertUtil.isTrue(packageAddress.getId() != null && packageAddress.getId() > 0, "更新数据不存在");
        changeDefaultAddr(packageAddress);
        return baseMapper.updateById(packageAddress.convertThis(PackageAddress.class));
    }

    /**
     * 批量删除package - 交货管理 - 地址信息表模块
     *
     * @param ids 需要删除的package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    @Override
    public int deletePackageAddressByIds(List<String> ids) {
        setOtherDefaultAddr(ids);
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 当删除的ids中存在默认地址，设置其他地址未默认
     *
     * @param ids
     */
    private void setOtherDefaultAddr(List<String> ids) {
        //TODO 获取用户id
        baseMapper.update(null, Wrappers.<PackageAddress>lambdaUpdate()
                .eq(PackageAddress::getSellerCode, "")
                .notIn(CollectionUtils.isNotEmpty(ids), PackageAddress::getId, ids)
                .set(PackageAddress::getDefaultFlag, 1)
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
    public int deletePackageAddressById(String id) {
        setOtherDefaultAddr(Collections.singletonList(id));
        return baseMapper.deleteById(id);
    }


}

