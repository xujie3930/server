package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.ContainerInfoDto;
import com.szmsd.delivery.dto.DelOutboundPackingDto;
import com.szmsd.delivery.vo.DelOutboundPackingVO;

import java.util.List;

/**
 * <p>
 * 装箱信息 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-23
 */
public interface IDelOutboundPackingService extends IService<DelOutboundPacking> {

    /**
     * 查询装箱信息模块
     *
     * @param id 装箱信息模块ID
     * @return 装箱信息模块
     */
    DelOutboundPacking selectDelOutboundPackingById(String id);

    /**
     * 查询装箱信息模块列表
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 装箱信息模块集合
     */
    List<DelOutboundPacking> selectDelOutboundPackingList(DelOutboundPacking delOutboundPacking);

    /**
     * 新增装箱信息模块
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 结果
     */
    int insertDelOutboundPacking(DelOutboundPacking delOutboundPacking);

    /**
     * 修改装箱信息模块
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 结果
     */
    int updateDelOutboundPacking(DelOutboundPacking delOutboundPacking);

    /**
     * 批量删除装箱信息模块
     *
     * @param ids 需要删除的装箱信息模块ID
     * @return 结果
     */
    int deleteDelOutboundPackingByIds(List<String> ids);

    /**
     * 删除装箱信息模块信息
     *
     * @param id 装箱信息模块ID
     * @return 结果
     */
    int deleteDelOutboundPackingById(String id);

    /**
     * 保存装箱信息
     *
     * @param orderNo  orderNo
     * @param packings packings
     * @param deleted  deleted
     */
    void save(String orderNo, List<DelOutboundPackingDto> packings, boolean deleted);

    /**
     * 删除装箱信息
     *
     * @param orderNo orderNo
     */
    void deleted(String orderNo);

    /**
     * 删除装箱信息
     *
     * @param orderNos orderNos
     */
    void deleted(List<String> orderNos);

    /**
     * 查询装箱信息
     *
     * @param orderNo orderNo
     * @param type    type
     * @return List<DelOutboundPackingVO>
     */
    List<DelOutboundPackingVO> listByOrderNo(String orderNo, int type);

    /**
     * 保存装箱信息
     *
     * @param orderNo       orderNo
     * @param containerList containerList
     */
    void save(String orderNo, List<ContainerInfoDto> containerList);
}

