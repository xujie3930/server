package com.szmsd.http.api.service;

import com.szmsd.http.dto.AddShipmentRuleRequest;
import com.szmsd.http.vo.BaseOperationResponse;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 15:34
 */
public interface IHtpIBasClientService {

    /**
     * #A4 新增/修改发货规则（物流服务）
     *
     * @param addShipmentRuleRequest addShipmentRuleRequest
     * @return BaseOperationResponse
     */
    BaseOperationResponse shipmentRule(AddShipmentRuleRequest addShipmentRuleRequest);
}
