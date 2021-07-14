package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundCombination;
import com.szmsd.delivery.dto.DelOutboundCombinationDto;
import com.szmsd.delivery.vo.DelOutboundCombinationVO;

import java.util.List;

/**
 * <p>
 * 出库单组合信息 服务类
 * </p>
 *
 * @author asd
 * @since 2021-07-02
 */
public interface IDelOutboundCombinationService extends IService<DelOutboundCombination> {

    /**
     * 查询出库单组合信息模块
     *
     * @param id 出库单组合信息模块ID
     * @return 出库单组合信息模块
     */
    DelOutboundCombination selectDelOutboundCombinationById(String id);

    /**
     * 查询出库单组合信息模块列表
     *
     * @param delOutboundCombination 出库单组合信息模块
     * @return 出库单组合信息模块集合
     */
    List<DelOutboundCombination> selectDelOutboundCombinationList(DelOutboundCombination delOutboundCombination);

    /**
     * 新增出库单组合信息模块
     *
     * @param delOutboundCombination 出库单组合信息模块
     * @return 结果
     */
    int insertDelOutboundCombination(DelOutboundCombination delOutboundCombination);

    /**
     * 修改出库单组合信息模块
     *
     * @param delOutboundCombination 出库单组合信息模块
     * @return 结果
     */
    int updateDelOutboundCombination(DelOutboundCombination delOutboundCombination);

    /**
     * 批量删除出库单组合信息模块
     *
     * @param ids 需要删除的出库单组合信息模块ID
     * @return 结果
     */
    int deleteDelOutboundCombinationByIds(List<String> ids);

    /**
     * 删除出库单组合信息模块信息
     *
     * @param id 出库单组合信息模块ID
     * @return 结果
     */
    int deleteDelOutboundCombinationById(String id);

    /**
     * 保存
     *
     * @param orderNo      orderNo
     * @param combinations combinations
     */
    void save(String orderNo, List<DelOutboundCombinationDto> combinations);

    /**
     * 修改
     *
     * @param orderNo      orderNo
     * @param combinations combinations
     */
    void update(String orderNo, List<DelOutboundCombinationDto> combinations);

    /**
     * 删除
     *
     * @param orderNo orderNo
     */
    void deleted(String orderNo);

    /**
     * 批量删除
     *
     * @param orderNos orderNos
     */
    void deleted(List<String> orderNos);

    /**
     * 查询
     *
     * @param orderNo orderNo
     * @return List
     */
    List<DelOutboundCombinationVO> listByOrderNo(String orderNo);

}

