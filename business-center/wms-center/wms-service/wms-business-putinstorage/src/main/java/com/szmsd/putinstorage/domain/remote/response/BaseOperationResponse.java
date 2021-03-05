package com.szmsd.putinstorage.domain.remote.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BaseOperationResponse {

    /** 是否执行成功 **/
    private Boolean success;

    /** 返回消息 **/
    private String message;

}
