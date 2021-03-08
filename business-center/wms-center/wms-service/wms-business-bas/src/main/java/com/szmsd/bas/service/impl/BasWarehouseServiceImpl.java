package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.component.RemoteComponent;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BasWarehouseCus;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.bas.dto.BasWarehouseQueryDTO;
import com.szmsd.bas.dto.BasWarehouseStatusChangeDTO;
import com.szmsd.bas.mapper.BasWarehouseMapper;
import com.szmsd.bas.service.IBasWarehouseService;
import com.szmsd.bas.vo.BasWarehouseInfoVO;
import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.system.api.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
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
     * @param queryDTO
     * @return
     */
    @Override
    public List<BasWarehouseVO> selectList(BasWarehouseQueryDTO queryDTO) {
        return baseMapper.selectList(queryDTO);
    }

    /**
     * 创建/更新仓库
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
     * @param warehouseNo
     * @return
     */
    @Override
    public BasWarehouseInfoVO queryInfo(String warehouseNo) {
        log.info("查询仓库详情：warehouseNo={}", warehouseNo);
        BasWarehouseInfoVO basWarehouseInfoVO = baseMapper.selectInfo(null, warehouseNo);
        if (basWarehouseInfoVO == null) {
            return null;
        }
        // 查询黑白名单客户
        List<BasWarehouseCus> basWarehouseCusList = baseMapper.selectWarehouseCus(warehouseNo, null);
        List<BasWarehouseCus> collect0 = basWarehouseCusList.stream().filter(item -> "0".equals(item.getExpress())).collect(Collectors.toList());
        basWarehouseInfoVO.setBlackCusList(collect0);
        List<BasWarehouseCus> collect1 = basWarehouseCusList.stream().filter(item -> "1".equals(item.getExpress())).collect(Collectors.toList());
        basWarehouseInfoVO.setWhiteCusList(collect1);
        log.info("查询仓库详情：查询完成{}", basWarehouseInfoVO);
        return basWarehouseInfoVO;
    }

    /**
     * 更新仓库客户黑白名单
     * @param basWarehouseCusList
     */
    @Override
    public void saveWarehouseCus(List<BasWarehouseCus> basWarehouseCusList) {
        log.info("更新仓库客户黑白名单：{}", basWarehouseCusList);
        SysUser loginUserInfo = remoteComponent.getLoginUserInfo();
        basWarehouseCusList.forEach(item -> {
            item.setCreateBy(loginUserInfo.getUserId() + "");
            item.setCreateByName(loginUserInfo.getUserName());
        });
        basWarehouseCusList.forEach(item -> {
            try {
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
     * @param basWarehouseStatusChangeDTO
     */
    @Override
    public void statusChange(BasWarehouseStatusChangeDTO basWarehouseStatusChangeDTO) {
        log.info("仓库状态变更：{}", basWarehouseStatusChangeDTO);
        BasWarehouse basWarehouse = new BasWarehouse().setWarehouseCode(basWarehouseStatusChangeDTO.getWarehouseCode()).setStatus(basWarehouseStatusChangeDTO.getStatus());
        baseMapper.update(basWarehouse, new UpdateWrapper<BasWarehouse>().lambda().eq(BasWarehouse::getWarehouseCode, basWarehouse.getWarehouseCode()));
        log.info("仓库状态变更：操作完成");
    }
}

