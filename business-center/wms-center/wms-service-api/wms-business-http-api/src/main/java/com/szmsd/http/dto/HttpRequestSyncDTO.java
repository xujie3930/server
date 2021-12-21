package com.szmsd.http.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.http.enums.RemoteConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class HttpRequestSyncDTO extends HttpRequestDto implements Serializable {

    /**
     * 上传文件类型
     */
    private RemoteConstant.RemoteTypeEnum remoteTypeEnum = RemoteConstant.RemoteTypeEnum.DEFAULT;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
