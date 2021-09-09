package com.szmsd.finance.compont;

import com.szmsd.bas.api.domain.BasSub;

/**
 * @ClassName: IRemoteApi
 * @Description:
 * @Author: 11
 * @Date: 2021-09-09 13:39
 */
public interface IRemoteApi {
    BasSub getSubCodeObj(String mainCode, String subName);

    String getSubCode(String mainCode, String subName);

    String getSubCodeOrElseBlack(String mainCode, String subName);
}
