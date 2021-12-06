package com.szmsd.chargerules.config;

import com.szmsd.bas.api.domain.BasSub;

import java.util.List;

/**
 * @ClassName: IRemoteApi
 * @Description:
 * @Author: 11
 * @Date: 2021-09-09 13:39
 */
public interface IRemoteApi {
    BasSub getSubCodeObj(String mainCode, String subName);

    String getSubNameByValue(String mainCode, String subValue);

    String getSubCodeObjSubCode(String mainCode, String subName);

    List<String> genNo(Integer count);

    String getSubCode(String mainCode, String subName);

    String getSubCodeOrElseBlack(String mainCode, String subName);

    /**
     * 获取仓库编码
     * @param wareHouseName
     * @return
     */
    String getWareHouseName(String wareHouseName);

    /**
     * 校验用户id
     * @param cusCode
     */
    boolean checkCusCode(String cusCode);
}
