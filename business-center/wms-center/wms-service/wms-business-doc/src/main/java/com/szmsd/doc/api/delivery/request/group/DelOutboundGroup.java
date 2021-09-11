package com.szmsd.doc.api.delivery.request.group;

/**
 * 出库验证组
 *
 * @author zhangyuyuan
 * @date 2021-08-03 10:14
 */
public interface DelOutboundGroup {

    interface Default {

    }

    /**
     * 转运出库
     */
    interface PackageTransfer extends Default {

    }

    interface Normal extends Default {

    }

    interface Destroy extends Default {

    }

    interface SelfPick extends Default {

    }

    interface Collection extends Default {

    }

    interface Batch extends Default {

    }

}
