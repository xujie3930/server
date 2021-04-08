package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.component.RemoteComponent;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BasWarehouseCus;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.mapper.BasWarehouseMapper;
import com.szmsd.bas.service.IBasWarehouseService;
import com.szmsd.bas.vo.BasWarehouseInfoVO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BasWarehouseServiceImpl extends ServiceImpl<BasWarehouseMapper, BasWarehouse> implements IBasWarehouseService {

    @Resource
    private RemoteComponent remoteComponent;

    /**
     * 仓库列表查询
     *
     * @param queryDTO
     * @return
     */
    @Override
    public List<BasWarehouseVO> selectList(BasWarehouseQueryDTO queryDTO) {
        return baseMapper.selectListVO(queryDTO);
    }

    /**
     * 创建/更新仓库
     *
     * @param addWarehouseRequest
     */
    @Override
    public void saveOrUpdate(AddWarehouseRequest addWarehouseRequest) {
        log.info("创建/更新仓库: {}", addWarehouseRequest);
        String warehouseCode = addWarehouseRequest.getWarehouseCode();
        BasWarehouse warehouse = baseMapper.selectOne(new QueryWrapper<BasWarehouse>().lambda().eq(BasWarehouse::getWarehouseCode, warehouseCode));
        BasWarehouse basWarehouse = BeanMapperUtil.map(addWarehouseRequest, BasWarehouse.class);
        if (warehouse == null) {
            baseMapper.insert(basWarehouse);
        } else {
            baseMapper.update(basWarehouse, new UpdateWrapper<BasWarehouse>().lambda().eq(BasWarehouse::getWarehouseCode, warehouseCode));
        }
        log.info("创建/更新仓库: 操作完成");
    }

    /**
     * 仓库详情 【包含黑白名单客户】
     *
     * @param warehouseCode
     * @return
     */
    @Override
    public BasWarehouseInfoVO queryInfo(String warehouseCode) {
        log.info("查询仓库详情：warehouseCode={}", warehouseCode);
        BasWarehouseInfoVO basWarehouseInfoVO = baseMapper.selectInfo(null, warehouseCode);
        if (basWarehouseInfoVO == null) {
            return null;
        }
        // 查询黑白名单客户
        List<BasWarehouseCus> basWarehouseCusList = baseMapper.selectWarehouseCus(warehouseCode, null);
        // 黑名单
        List<BasWarehouseCus> blackCusList = basWarehouseCusList.stream().filter(item -> "0".equals(item.getExpress())).collect(Collectors.toList());
        basWarehouseInfoVO.setBlackCusList(blackCusList);
        String blackCus = blackCusList.stream().map(BasWarehouseCus::getCusCode).collect(Collectors.joining(","));
        basWarehouseInfoVO.setBlackCus(blackCus);
        // 白名单
        List<BasWarehouseCus> whiteCusList = basWarehouseCusList.stream().filter(item -> "1".equals(item.getExpress())).collect(Collectors.toList());
        basWarehouseInfoVO.setWhiteCusList(whiteCusList);
        String whiteCus = whiteCusList.stream().map(BasWarehouseCus::getCusCode).collect(Collectors.joining(","));
        basWarehouseInfoVO.setWhiteCus(whiteCus);

        log.info("查询仓库详情：查询完成{}", basWarehouseInfoVO);
        return basWarehouseInfoVO;
    }

    /**
     * 更新仓库客户黑白名单
     *
     * @param basWarehouseCusDTO
     */
    @Override
    public void saveWarehouseCus(BasWarehouseCusDTO basWarehouseCusDTO) {
        log.info("更新仓库客户黑白名单：{}", basWarehouseCusDTO);
        String warehouseCode = basWarehouseCusDTO.getWarehouseCode();
        baseMapper.deleteWarehouseCus(warehouseCode);
        List<BasWarehouseCus> basWarehouseCusList = basWarehouseCusDTO.getWarehouseCusList();
        if (CollectionUtils.isEmpty(basWarehouseCusList)) {
            log.info("清空黑白名单, warehouseCode={}", warehouseCode);
            return;
        }
        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        basWarehouseCusList.forEach(item -> item.setCreateBy(loginUserInfo.getUserId() + "").setCreateByName(loginUserInfo.getUserName()));
        basWarehouseCusList.forEach(item -> {
            try {
                item.setWarehouseCode(warehouseCode);
                baseMapper.insertWarehouseCus(item);
            } catch (Exception e) {
                // 唯一索引 warehouseCode cusCode
                log.info(e.getMessage());
            }
        });
        log.info("更新仓库客户黑白名单：操作完成");
    }

    /**
     * 状态修改
     *
     * @param basWarehouseStatusChangeDTO
     */
    @Override
    public void statusChange(BasWarehouseStatusChangeDTO basWarehouseStatusChangeDTO) {
        log.info("仓库状态变更：{}", basWarehouseStatusChangeDTO);
        BasWarehouse basWarehouse = new BasWarehouse().setWarehouseCode(basWarehouseStatusChangeDTO.getWarehouseCode()).setStatus(basWarehouseStatusChangeDTO.getStatus());
        baseMapper.update(basWarehouse, new UpdateWrapper<BasWarehouse>().lambda().eq(BasWarehouse::getWarehouseCode, basWarehouse.getWarehouseCode()));
        log.info("仓库状态变更：操作完成");
    }

    /**
     * 查询所有的仓库下拉
     * @return
     */
    @Override
    public List<WarehouseKvDTO> selectInboundWarehouse() {
        List<BasWarehouseVO> basWarehouseVOS = this.selectList(new BasWarehouseQueryDTO().setStatus("1"));
        List<WarehouseKvDTO> collect = basWarehouseVOS.stream().map(item -> new WarehouseKvDTO().setKey(item.getWarehouseCode()).setValue(item.getWarehouseCode()).setCountry(item.getCountryCode())).collect(Collectors.toList());
        return collect;
    }

    /**
     * 查询入库单 - 创建 - 目的仓库下拉 【过滤出有效仓库、当前登录人没在黑名单、并且白名单非空或白名单包含当前登录人】
     * @return
     */
    @Override
    public List<WarehouseKvDTO> selectCusInboundWarehouse() {
        List<BasWarehouseVO> basWarehouseVOS = this.selectList(new BasWarehouseQueryDTO().setStatus("1"));
        SysUser user = remoteComponent.getLoginUserInfo();
        List<WarehouseKvDTO> collect = basWarehouseVOS.stream().filter(basWarehouseVO -> this.vailCusWarehouse(basWarehouseVO.getWarehouseCode(), user)).map(item -> new WarehouseKvDTO().setKey(item.getWarehouseCode()).setValue(item.getWarehouseCode()).setCountry(item.getCountryCode())).collect(Collectors.toList());
        return collect;
    }

    /**
     * 判断当前登录人是是否能使用这个仓库
     * @param warehouseCode
     * @return
     */
    @Override
    public boolean vailCusWarehouse(String warehouseCode) {
        return vailCusWarehouse(warehouseCode, remoteComponent.getLoginUserInfo());
    }


    /**
     * 判断user是是否能使用这个仓库
     * @param warehouseCode
     * @return
     */
    @Override
    public boolean vailCusWarehouse(String warehouseCode, SysUser user) {
        List<BasWarehouseCus> basWarehouseCusList = baseMapper.selectWarehouseCus(warehouseCode, null);
        // 在黑名单里面 return false
        List<BasWarehouseCus> collect0 = basWarehouseCusList.stream().filter(item -> "0".equals(item.getExpress()) && item.getCusCode().equals(user.getSellerCode())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect0)) {
            return false;
        }
        // 白名单不为空的话 判断是否在白名单 如果不在 return false
        List<BasWarehouseCus> collect1 = basWarehouseCusList.stream().filter(item -> "1".equals(item.getExpress())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect1)) {
            List<BasWarehouseCus> collect3 = basWarehouseCusList.stream().filter(item -> item.getCusCode().equals(user.getSellerCode())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(collect3)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BasWarehouse queryByWarehouseCode(String warehouseCode) {
        LambdaQueryWrapper<BasWarehouse> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BasWarehouse::getWarehouseCode, warehouseCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<BasWarehouse> queryByWarehouseCodes(List<String> warehouseCodes) {
        LambdaQueryWrapper<BasWarehouse> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(BasWarehouse::getWarehouseCode, warehouseCodes);
        return this.list(queryWrapper);
    }
}

