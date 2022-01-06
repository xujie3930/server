package com.szmsd.chargerules.service;


import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundOperationVO;

public interface IOperationService {


    /**
     * 出库扣款
     *
     * @param dto dto
     * @return result
     */
    R delOutboundDeductions(DelOutboundOperationVO dto);

    /**
     * 出库冻结余额
     *
     * @param delOutboundVO delOutboundVO
     * @return result
     */
    R delOutboundFreeze(DelOutboundOperationVO delOutboundVO);

    /**
     * 出库解冻余额
     *
     * @param delOutboundVO delOutboundVO
     * @return result
     */
    R delOutboundThaw(DelOutboundOperationVO delOutboundVO);

}
