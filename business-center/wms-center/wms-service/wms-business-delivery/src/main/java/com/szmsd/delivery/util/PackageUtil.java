package com.szmsd.delivery.util;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 19:37
 */
public final class PackageUtil {

    private PackageUtil() {
    }

    /**
     * 计算大包裹的体积
     *
     * @param packageInfoList packageInfoList
     * @return PackageInfo
     */
    public static PackageInfo count(List<PackageInfo> packageInfoList) {
        /*
         * 1. 两件货物合并，两长取最大当长，两宽取最大当宽，高相加当高。把它再当成一个整体，和第三件货物合并，同上，以此类推
         * 2. 3边最长的当长，第二的当宽，高是最小的
         * 3. 合并之后可能会重新定义长宽高
         */
        PackageInfo packageInfo = packageInfoList.get(0).reset();
        for (int i = 1; i < packageInfoList.size(); i++) {
            packageInfo.join(packageInfoList.get(i).reset());
        }
        return packageInfo;
    }
}
