package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 11:43
 */
@Data
@Accessors(chain = true)
public class UserIdentity implements Serializable {

    // 用户Id
    private String userId;

    // 用户名
    private String userName;

    // 姓名
    private String fullName;
}
