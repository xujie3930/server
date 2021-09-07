package com.szmsd.delivery.service.impl;

import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundBringVerifyDto;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.service.IDelOutboundDocService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundBringVerifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:32
 */
@Service
public class DelOutboundDocServiceImpl implements IDelOutboundDocService {

    @Autowired
    private IDelOutboundService delOutboundService;

    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;

    @Override
    public List<DelOutboundAddResponse> add(List<DelOutboundDto> list) {

        // 批量创建出库单
        List<DelOutboundAddResponse> responses = this.delOutboundService.insertDelOutbounds(list);

        // 获取出库单ID
        List<Long> ids = responses.stream().map(DelOutboundAddResponse::getId).collect(Collectors.toList());

        // 批量提审出库单
        DelOutboundBringVerifyDto bringVerifyDto = new DelOutboundBringVerifyDto();
        bringVerifyDto.setIds(ids);
        List<DelOutboundBringVerifyVO> bringVerifyVOList = this.delOutboundBringVerifyService.bringVerify(bringVerifyDto);

        Map<String, DelOutboundBringVerifyVO> bringVerifyVOMap = new HashMap<>();
        for (DelOutboundBringVerifyVO bringVerifyVO : bringVerifyVOList) {
            bringVerifyVOMap.put(bringVerifyVO.getOrderNo(), bringVerifyVO);
        }

        // 查询出库单信息
        List<DelOutbound> delOutboundList = this.delOutboundService.listByIds(ids);
        Map<Long, DelOutbound> delOutboundMap = delOutboundList.stream().collect(Collectors.toMap(DelOutbound::getId, v -> v, (a, b) -> a));

        for (DelOutboundAddResponse response : responses) {
            // 提审结果
            DelOutboundBringVerifyVO bringVerifyVO = bringVerifyVOMap.get(response.getOrderNo());
            if (null != bringVerifyVO) {
                response.setStatus(bringVerifyVO.getSuccess());
                response.setMessage(bringVerifyVO.getMessage());
            }
            // 挂号
            DelOutbound delOutbound = delOutboundMap.get(response.getId());
            if (null != delOutbound) {
                response.setTrackingNo(delOutbound.getTrackingNo());
            }
        }

        return responses;
    }
}
