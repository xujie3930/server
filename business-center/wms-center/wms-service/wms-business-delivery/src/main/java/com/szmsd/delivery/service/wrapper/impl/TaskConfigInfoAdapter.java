package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.http.dto.TaskConfigInfo;

public class TaskConfigInfoAdapter {

    /**
     * 自提：不接收发货指令、波次打印（需要把客户上传的PDF在一开始创建WMS订单的时候推送）、包材回传：否、重量回传：否
     * <p>
     * 组合SKU：不接收发货指令、波次打印（需要把客户上传的PDF在一开始创建WMS订单的时候推送）、包材回传：是、重量回传：否
     * <p>
     * 拆分SKU：不接收发货指令、波次打印（需要把客户上传的PDF在一开始创建WMS订单的时候推送）、包材回传：是、重量回传：否
     * <p>
     * 销毁的：不接收发货指令、不打印、包材回传：否、重量回传：否
     *
     * @param orderType 订单类型
     * @return 发货计划
     */
    public static TaskConfigInfo getTaskConfigInfo(String orderType) {
        if (DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(orderType)) {
            return buildTaskConfigInfo("NotReceive", false, false, "PrintOnWave");
        } else if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(orderType)) {
            return buildTaskConfigInfo("NotReceive", true, false, "PrintOnWave");
        } else if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(orderType)) {
            return buildTaskConfigInfo("NotReceive", true, false, "PrintOnWave");
        } else if (DelOutboundOrderTypeEnum.DESTROY.getCode().equals(orderType)) {
            return buildTaskConfigInfo("NotReceive", false, false, "NotPrint");
        }
        return null;
    }

    public static TaskConfigInfo buildTaskConfigInfo(String receiveShippingType, Boolean isPublishPackageMaterial, Boolean isPublishPackageWeight, String printShippingLabelType) {
        TaskConfigInfo taskConfigInfo = new TaskConfigInfo();
        taskConfigInfo.setReceiveShippingType(receiveShippingType);
        taskConfigInfo.setIsPublishPackageMaterial(isPublishPackageMaterial);
        taskConfigInfo.setIsPublishPackageWeight(isPublishPackageWeight);
        taskConfigInfo.setPrintShippingLabelType(printShippingLabelType);
        return taskConfigInfo;
    }
}
