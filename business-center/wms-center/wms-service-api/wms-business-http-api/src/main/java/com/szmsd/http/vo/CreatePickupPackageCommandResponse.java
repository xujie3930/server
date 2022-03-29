package com.szmsd.http.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CreatePickupPackageCommandResponse extends ResponseVO {

    /**
     * 流水号
     */
    private String referenceNumber;

    /**
     * 提货单号
     */
    private String pickupPackageNumber;

}
