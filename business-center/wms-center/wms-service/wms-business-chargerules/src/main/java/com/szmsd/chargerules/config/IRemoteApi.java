package com.szmsd.chargerules.config;

import com.szmsd.bas.api.domain.BasSub;
import reactor.util.function.Tuple2;

import java.util.List;

/**
 * @ClassName: IRemoteApi
 * @Description:
 * @Author: 11
 * @Date: 2021-09-09 13:39
 */
public interface IRemoteApi {

    /**
     * 通过子类别名称获取子类别基础信息
     * @param mainCode 主类别code
     * @param subName 子类别名
     * @return 子类别基础信息
     */
    BasSub getSubObjBySubName(String mainCode, String subName);

    BasSub getSubObjBySubCode(String mainCode, String subCode);

    /**
     * 通过子类别值获取子类别名
     * @param mainCode 主类别code
     * @param subValue 子类别值
     * @return  子类别名
     */
    String getSubNameByValue(String mainCode, String subValue);

    /**
     *
     * @param mainCode
     * @param subCode
     * @return
     */
    String getSubNameBySubCode(String mainCode, String subCode);

    String getSubCodeBySubName(String mainCode, String subName);

    List<String> genNo(Integer count);

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

    Tuple2<String, String> getCusCodeAndCusName(String cusCodeList);
}
