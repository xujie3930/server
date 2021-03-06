package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.constant.SerialNumberConstant;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 出库单 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Service
public class DelOutboundServiceImpl extends ServiceImpl<DelOutboundMapper, DelOutbound> implements IDelOutboundService {

    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    @Autowired
    private SerialNumberClientService serialNumberClientService;

    /**
     * 查询出库单模块
     *
     * @param id 出库单模块ID
     * @return 出库单模块
     */
    @Override
    public DelOutbound selectDelOutboundById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询出库单模块列表
     *
     * @param delOutbound 出库单模块
     * @return 出库单模块
     */
    @Override
    public List<DelOutbound> selectDelOutboundList(DelOutbound delOutbound) {
        QueryWrapper<DelOutbound> where = new QueryWrapper<DelOutbound>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增出库单模块
     *
     * @param dto 出库单模块
     * @return 结果
     */
    @Transactional
    @Override
    public int insertDelOutbound(DelOutboundDto dto) {
        if (!DelOutboundOrderTypeEnum.has(dto.getOrderType())) {
            throw new CommonException("999", "订单类型不存在");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            throw new CommonException("999", "获取登录用户信息失败");
        }

        DelOutbound delOutbound = BeanMapperUtil.map(dto, DelOutbound.class);
        // 从登录人信息中获取客户代码
        delOutbound.setCustomCode("");
        // 生成出库单号
        // 流水号规则：CK + 客户代码 + （年月日 + 8位流水）
        delOutbound.setOrderNo("CK" + delOutbound.getCustomCode() + this.serialNumberClientService.generateNumber(SerialNumberConstant.DEL_OUTBOUND_NO));
        // 默认状态
        delOutbound.setState(DelOutboundStateEnum.REVIEWED.getCode());
        // 保存出库单
        int insert = baseMapper.insert(delOutbound);

        if (insert == 0) {
            throw new CommonException("999", "保存出库单失败！");
        }

        // 保存地址
        List<DelOutboundAddress> delOutboundAddressList = BeanMapperUtil.mapList(dto.getAddress(), DelOutboundAddress.class);
        if (CollectionUtils.isNotEmpty(delOutboundAddressList)) {
            for (DelOutboundAddress delOutboundAddress : delOutboundAddressList) {
                delOutboundAddress.setOrderNo(delOutbound.getOrderNo());
            }
            this.delOutboundAddressService.saveBatch(delOutboundAddressList);
        }

        // 保存明细
        List<DelOutboundDetail> delOutboundDetailList = BeanMapperUtil.mapList(dto.getDetails(), DelOutboundDetail.class);
        if (CollectionUtils.isNotEmpty(delOutboundDetailList)) {
            for (DelOutboundDetail delOutboundDetail : delOutboundDetailList) {
                delOutboundDetail.setOrderNo(delOutbound.getOrderNo());
            }
            this.delOutboundDetailService.saveBatch(delOutboundDetailList);
        }

        return insert;
    }

    /**
     * 修改出库单模块
     *
     * @param dto 出库单模块
     * @return 结果
     */
    @Transactional
    @Override
    public int updateDelOutbound(DelOutboundDto dto) {
        DelOutbound inputDelOutbound = BeanMapperUtil.map(dto, DelOutbound.class);
        DelOutbound delOutbound = this.getById(inputDelOutbound.getId());
        if (null == delOutbound) {
            throw new CommonException("999", "单据不存在");
        }

        // 先删后增


        return baseMapper.updateById(inputDelOutbound);
    }

    /**
     * 批量删除出库单模块
     *
     * @param ids 需要删除的出库单模块ID
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDelOutboundByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除出库单模块信息
     *
     * @param id 出库单模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundById(String id) {
        return baseMapper.deleteById(id);
    }


}

