package com.szmsd.http.config.inner.api;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:02
 */
public class SaaSCarrierServiceAdminApiConfig implements ApiConfig {

    /**
     * ShipmentOrder
     */
    private ShipmentOrder shipmentOrder;

    public ShipmentOrder getShipmentOrder() {
        return shipmentOrder;
    }

    public void setShipmentOrder(ShipmentOrder shipmentOrder) {
        this.shipmentOrder = shipmentOrder;
    }

    public static class ShipmentOrder {
        // 获取可用的承运商服务名称(管理端)
        private String services;
        // 创建承运商物流订单（客户端）
        private String create;
        // 获取承运商物流订单信息（管理端）
        private String searching;
        // 取消承运商物流订单（客户端）
        private String cancellation;
        // 取消承运商物流订单查询记录（管理端）
        private String cancellationSearching;
        // 处理订单标签,这个暂时用不了（客户端）
        private String labelsMerging;
        // 根据订单号返回标签文件流（客户端）
        private String label;
        // 根据标签文件路径去获取文件流
        private String labelUrl;

        public String getServices() {
            return services;
        }

        public void setServices(String services) {
            this.services = services;
        }

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getSearching() {
            return searching;
        }

        public void setSearching(String searching) {
            this.searching = searching;
        }

        public String getCancellation() {
            return cancellation;
        }

        public void setCancellation(String cancellation) {
            this.cancellation = cancellation;
        }

        public String getCancellationSearching() {
            return cancellationSearching;
        }

        public void setCancellationSearching(String cancellationSearching) {
            this.cancellationSearching = cancellationSearching;
        }

        public String getLabelsMerging() {
            return labelsMerging;
        }

        public void setLabelsMerging(String labelsMerging) {
            this.labelsMerging = labelsMerging;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLabelUrl() {
            return labelUrl;
        }

        public void setLabelUrl(String labelUrl) {
            this.labelUrl = labelUrl;
        }
    }
}
