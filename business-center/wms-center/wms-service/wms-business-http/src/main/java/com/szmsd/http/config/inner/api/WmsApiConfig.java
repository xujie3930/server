package com.szmsd.http.config.inner.api;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 11:51
 */
public class WmsApiConfig implements ApiConfig {

    /**
     * BaseInfo
     */
    private BaseInfo baseInfo;

    /**
     * Exception
     */
    private Exception exception;

    /**
     * Inbound
     */
    private Inbound inbound;

    /**
     * Inventory
     */
    private Inventory inventory;

    /**
     * Outbound
     */
    private Outbound outbound;

    /**
     * Return
     */
    private Returned returned;

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Inbound getInbound() {
        return inbound;
    }

    public void setInbound(Inbound inbound) {
        this.inbound = inbound;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Outbound getOutbound() {
        return outbound;
    }

    public void setOutbound(Outbound outbound) {
        this.outbound = outbound;
    }

    public Returned getReturned() {
        return returned;
    }

    public void setReturned(Returned returned) {
        this.returned = returned;
    }

     static class BaseInfo {
        // A1 新增/修改卖家
        private String seller;
        // A2 新增/修改包材信息
        private String packings;
        // A3 新增/修改 产品（SKU）
        private String products;
        // A4 新增/修改发货规则（物流服务）
        private String shipmentRule;
        // A5 新增验货请求
        private String inspection;
        // A6 新增/修改特殊操作类型
        private String operationType;
        // A7 更新特殊操作结果
        private String operationResult;

        public String getSeller() {
            return seller;
        }

        public void setSeller(String seller) {
            this.seller = seller;
        }

        public String getPackings() {
            return packings;
        }

        public void setPackings(String packings) {
            this.packings = packings;
        }

        public String getProducts() {
            return products;
        }

        public void setProducts(String products) {
            this.products = products;
        }

        public String getShipmentRule() {
            return shipmentRule;
        }

        public void setShipmentRule(String shipmentRule) {
            this.shipmentRule = shipmentRule;
        }

        public String getInspection() {
            return inspection;
        }

        public void setInspection(String inspection) {
            this.inspection = inspection;
        }

        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        public String getOperationResult() {
            return operationResult;
        }

        public void setOperationResult(String operationResult) {
            this.operationResult = operationResult;
        }
    }

    static class Exception {
        // /api/exception/processing
        private String processing;

        public String getProcessing() {
            return processing;
        }

        public void setProcessing(String processing) {
            this.processing = processing;
        }
    }

    static class Inbound {
        // B1 创建入库单
        private String create;
        // B2 取消入库单
        private String cancel;
        // B3 创建转运单
        private String createPackage;

        public String getCreatePackage() {
            return createPackage;
        }

        public void setCreatePackage(String createPackage) {
            this.createPackage = createPackage;
        }

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getCancel() {
            return cancel;
        }

        public void setCancel(String cancel) {
            this.cancel = cancel;
        }
    }

    static class Inventory {
        // I2 获取库存
        private String listing;
        // I3 创建/修改盘点单
        private String counting;

        public String getListing() {
            return listing;
        }

        public void setListing(String listing) {
            this.listing = listing;
        }

        public String getCounting() {
            return counting;
        }

        public void setCounting(String counting) {
            this.counting = counting;
        }
    }

    static class Outbound {
        // C1 创建出库单
        private String create;
        // C2 取消出库单
        private String cancel;
        // C3 更新出库单挂号
        private String tracking;
        // C4 更新出库单标签
        private String label;
        // D2 更新出库单发货指令
        private String shipping;

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getCancel() {
            return cancel;
        }

        public void setCancel(String cancel) {
            this.cancel = cancel;
        }

        public String getTracking() {
            return tracking;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getShipping() {
            return shipping;
        }

        public void setShipping(String shipping) {
            this.shipping = shipping;
        }
    }

    static class Returned {
        // F1 创建退件预报
        private String expected;
        // F2 接收客户提供的处理方式
        private String processing;

        public String getExpected() {
            return expected;
        }

        public void setExpected(String expected) {
            this.expected = expected;
        }

        public String getProcessing() {
            return processing;
        }

        public void setProcessing(String processing) {
            this.processing = processing;
        }
    }
}
