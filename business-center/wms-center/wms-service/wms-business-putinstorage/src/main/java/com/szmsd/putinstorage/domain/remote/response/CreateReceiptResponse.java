package com.szmsd.putinstorage.domain.remote.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateReceiptResponse {

    /** 创建的单号 **/
   private String orderNo;

    /** 是否执行成功 **/
   private Boolean success;

    /** 返回消息 **/
   private String message;

}
