package com.szmsd.http.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CreateReceiptResponse extends ResponseVO {

    /** 创建的单号 **/
   private String orderNo;

}
