package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.domain.DelOutboundPackingDetail;
import com.szmsd.delivery.dto.ContainerDetailDto;
import com.szmsd.delivery.dto.ContainerInfoDto;
import com.szmsd.delivery.dto.DelOutboundPackingDetailDto;
import com.szmsd.delivery.dto.DelOutboundPackingDto;
import com.szmsd.delivery.enums.DelOutboundPackingTypeConstant;
import com.szmsd.delivery.mapper.DelOutboundPackingMapper;
import com.szmsd.delivery.service.IDelOutboundPackingDetailService;
import com.szmsd.delivery.service.IDelOutboundPackingService;
import com.szmsd.delivery.vo.DelOutboundPackingDetailVO;
import com.szmsd.delivery.vo.DelOutboundPackingVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 装箱信息 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-23
 */
@Service
public class DelOutboundPackingServiceImpl extends ServiceImpl<DelOutboundPackingMapper, DelOutboundPacking> implements IDelOutboundPackingService {

    private final IDelOutboundPackingDetailService delOutboundPackingDetailService;

    public DelOutboundPackingServiceImpl(IDelOutboundPackingDetailService delOutboundPackingDetailService) {
        this.delOutboundPackingDetailService = delOutboundPackingDetailService;
    }

    /**
     * 查询装箱信息模块
     *
     * @param id 装箱信息模块ID
     * @return 装箱信息模块
     */
    @Override
    public DelOutboundPacking selectDelOutboundPackingById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询装箱信息模块列表
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 装箱信息模块
     */
    @Override
    public List<DelOutboundPacking> selectDelOutboundPackingList(DelOutboundPacking delOutboundPacking) {
        QueryWrapper<DelOutboundPacking> where = new QueryWrapper<DelOutboundPacking>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增装箱信息模块
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 结果
     */
    @Override
    public int insertDelOutboundPacking(DelOutboundPacking delOutboundPacking) {
        return baseMapper.insert(delOutboundPacking);
    }

    /**
     * 修改装箱信息模块
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 结果
     */
    @Override
    public int updateDelOutboundPacking(DelOutboundPacking delOutboundPacking) {
        return baseMapper.updateById(delOutboundPacking);
    }

    /**
     * 批量删除装箱信息模块
     *
     * @param ids 需要删除的装箱信息模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundPackingByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除装箱信息模块信息
     *
     * @param id 装箱信息模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundPackingById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public void save(String orderNo, List<DelOutboundPackingDto> packings, boolean deleted) {
        if (deleted) {
            // 删除装箱信息
            this.deleted(orderNo);
        }
        if (CollectionUtils.isNotEmpty(packings)) {
            List<DelOutboundPackingDetail> details = new ArrayList<>();
            for (DelOutboundPackingDto packing : packings) {
                DelOutboundPacking p = BeanMapperUtil.map(packing, DelOutboundPacking.class);
                p.setOrderNo(orderNo);
                p.setType(DelOutboundPackingTypeConstant.TYPE_1);
                // 保存装箱信息
                this.save(p);
                List<DelOutboundPackingDetailDto> detailDtos = packing.getDetails();
                if (CollectionUtils.isNotEmpty(detailDtos)) {
                    List<DelOutboundPackingDetail> detailList = BeanMapperUtil.mapList(detailDtos, DelOutboundPackingDetail.class);
                    for (DelOutboundPackingDetail detail : detailList) {
                        detail.setOrderNo(orderNo);
                        detail.setPackingId(p.getId());
                    }
                    details.addAll(detailList);
                }
            }
            if (CollectionUtils.isNotEmpty(details)) {
                this.delOutboundPackingDetailService.saveBatch(details);
            }
        }
    }

    @Override
    public void deleted(String orderNo) {
        // 删除装箱信息
        this.remove(Wrappers.<DelOutboundPacking>lambdaQuery().eq(DelOutboundPacking::getOrderNo, orderNo));
        // 删除明细信息
        this.delOutboundPackingDetailService.remove(Wrappers.<DelOutboundPackingDetail>lambdaQuery().eq(DelOutboundPackingDetail::getOrderNo, orderNo));
    }

    @Override
    public void deleted(List<String> orderNos) {
        // 删除装箱信息
        this.remove(Wrappers.<DelOutboundPacking>lambdaQuery().in(DelOutboundPacking::getOrderNo, orderNos));
        // 删除明细信息
        this.delOutboundPackingDetailService.remove(Wrappers.<DelOutboundPackingDetail>lambdaQuery().in(DelOutboundPackingDetail::getOrderNo, orderNos));
    }

    @Override
    public List<DelOutboundPackingVO> listByOrderNo(String orderNo, int type) {
        // 查询装箱信息
        List<DelOutboundPacking> packingList = this.list(Wrappers.<DelOutboundPacking>lambdaQuery().eq(DelOutboundPacking::getOrderNo, orderNo).eq(DelOutboundPacking::getType, type));
        if (CollectionUtils.isEmpty(packingList)) {
            return Collections.emptyList();
        }
        // 查询明细信息
        List<DelOutboundPackingDetail> detailList = this.delOutboundPackingDetailService.list(Wrappers.<DelOutboundPackingDetail>lambdaQuery().eq(DelOutboundPackingDetail::getOrderNo, orderNo));
        Map<Long, List<DelOutboundPackingDetail>> detailMap;
        if (CollectionUtils.isNotEmpty(detailList)) {
            detailMap = detailList.stream().collect(Collectors.groupingBy(DelOutboundPackingDetail::getPackingId));
        } else {
            detailMap = Collections.emptyMap();
        }
        List<DelOutboundPackingVO> packingVOList = new ArrayList<>(packingList.size());
        for (DelOutboundPacking packing : packingList) {
            DelOutboundPackingVO packingVO = BeanMapperUtil.map(packing, DelOutboundPackingVO.class);
            List<DelOutboundPackingDetail> details = detailMap.get(packing.getId());
            List<DelOutboundPackingDetailVO> detailVOList;
            if (CollectionUtils.isNotEmpty(details)) {
                detailVOList = BeanMapperUtil.mapList(details, DelOutboundPackingDetailVO.class);
            } else {
                detailVOList = Collections.emptyList();
            }
            packingVO.setDetails(detailVOList);
            packingVOList.add(packingVO);
        }
        return packingVOList;
    }

    @Override
    public void save(String orderNo, List<ContainerInfoDto> containerList) {
        if (CollectionUtils.isNotEmpty(containerList)) {
            List<DelOutboundPackingDetail> details = new ArrayList<>();
            for (ContainerInfoDto infoDto : containerList) {
                DelOutboundPacking p = BeanMapperUtil.map(infoDto, DelOutboundPacking.class);
                p.setOrderNo(orderNo);
                p.setPackingNo(infoDto.getContainerCode());
                p.setType(DelOutboundPackingTypeConstant.TYPE_2);
                List<ContainerDetailDto> containerDetailList = infoDto.getContainerDetailList();
                // 累加SKU数量
                Long qty = 0L;
                if (CollectionUtils.isNotEmpty(containerDetailList)) {
                    for (ContainerDetailDto detailDto : containerDetailList) {
                        if (null == detailDto || null == detailDto.getQty()) {
                            continue;
                        }
                        qty += detailDto.getQty();
                    }
                }
                p.setQty(qty);
                // 保存装箱信息
                this.save(p);
                if (CollectionUtils.isNotEmpty(containerDetailList)) {
                    List<DelOutboundPackingDetail> detailList = BeanMapperUtil.mapList(containerDetailList, DelOutboundPackingDetail.class);
                    for (DelOutboundPackingDetail detail : detailList) {
                        detail.setOrderNo(orderNo);
                        detail.setPackingId(p.getId());
                    }
                    details.addAll(detailList);
                }
            }
            if (CollectionUtils.isNotEmpty(details)) {
                this.delOutboundPackingDetailService.saveBatch(details);
            }
        }
    }
}

