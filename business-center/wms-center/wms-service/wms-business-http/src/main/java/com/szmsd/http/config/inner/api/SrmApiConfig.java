package com.szmsd.http.config.inner.api;

public class SrmApiConfig implements ApiConfig {

    // 包裹成本获取接口
    private PackageCost packageCost;

    // 成本费用计算API
    private Pricing pricing;

    // 物流线路选择服务API
    private RoutePath routePath;

    public PackageCost getPackageCost() {
        return packageCost;
    }

    public void setPackageCost(PackageCost packageCost) {
        this.packageCost = packageCost;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public RoutePath getRoutePath() {
        return routePath;
    }

    public void setRoutePath(RoutePath routePath) {
        this.routePath = routePath;
    }

    static class PackageCost {
        // 批量获取包裹成本信息，其中处理号，最多支持1000个，多个请分开调用
        private String batch;

        public String getBatch() {
            return batch;
        }

        public void setBatch(String batch) {
            this.batch = batch;
        }
    }

    static class Pricing {
        // 根据供应商服务，计算包裹成本
        private String service;

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }
    }

    static class RoutePath {
        // 根据线路图编号，选择线路图
        private String route;

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }
    }
}
