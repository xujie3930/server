package com.szmsd.http.config.inner.api;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 13:50
 */
public class SaaSPricedProductApiConfig implements ApiConfig {

    /**
     * PricedGrade
     */
    private PricedGrade pricedGrade;

    /**
     * PricedProduct
     */
    private PricedProduct pricedProduct;

    /**
     * PricedSheet
     */
    private PricedSheet pricedSheet;

    public PricedGrade getPricedGrade() {
        return pricedGrade;
    }

    public void setPricedGrade(PricedGrade pricedGrade) {
        this.pricedGrade = pricedGrade;
    }

    public PricedProduct getPricedProduct() {
        return pricedProduct;
    }

    public void setPricedProduct(PricedProduct pricedProduct) {
        this.pricedProduct = pricedProduct;
    }

    public PricedSheet getPricedSheet() {
        return pricedSheet;
    }

    public void setPricedSheet(PricedSheet pricedSheet) {
        this.pricedSheet = pricedSheet;
    }

    static class PricedGrade {
        // 获取所有报价等级信息
        private String grades;
        // 创建报价等级信息
        private String create;
        // 分页查询报价等级列表，返回指定页面的数据，以及统计总记录数
        private String pageResult;
        // 修改报价等级信息
        private String update;
        // 删除报价等级信息
        private String delete;
        // 设置默认报价等级
        private String updateDefault;

        public String getGrades() {
            return grades;
        }

        public void setGrades(String grades) {
            this.grades = grades;
        }

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getPageResult() {
            return pageResult;
        }

        public void setPageResult(String pageResult) {
            this.pageResult = pageResult;
        }

        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }

        public String getDelete() {
            return delete;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }

        public String getUpdateDefault() {
            return updateDefault;
        }

        public void setUpdateDefault(String updateDefault) {
            this.updateDefault = updateDefault;
        }
    }

    static class PricedProduct {
        // 分页查询产品列表，返回指定页面的数据，以及统计总记录数
        private String pageResult;
        // 根据客户代码国家等信息获取可下单产品
        private String inService;
        // 查询产品下拉列表，返回list数据
        private String keyValuePairs;
        // 根据产品代码获取计价产品信息
        private String products;
        // 修改报价产品信息
        private String update;
        // 创建报价产品信息
        private String create;
        // 修改一个计价产品信息的报价表对应的等级和生效时间段
        private String updateGrade;
        // 计算包裹的费用
        private String pricing;
        // 批量计算包裹的费用
        private String pricingBatch;
        // 根据包裹基本信息获取可下单报价产品
        private String pricedProducts;
        // 导出产品信息列表
        private String exportFile;

        public String getPageResult() {
            return pageResult;
        }

        public void setPageResult(String pageResult) {
            this.pageResult = pageResult;
        }

        public String getInService() {
            return inService;
        }

        public void setInService(String inService) {
            this.inService = inService;
        }

        public String getKeyValuePairs() {
            return keyValuePairs;
        }

        public void setKeyValuePairs(String keyValuePairs) {
            this.keyValuePairs = keyValuePairs;
        }

        public String getProducts() {
            return products;
        }

        public void setProducts(String products) {
            this.products = products;
        }

        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getUpdateGrade() {
            return updateGrade;
        }

        public void setUpdateGrade(String updateGrade) {
            this.updateGrade = updateGrade;
        }

        public String getPricing() {
            return pricing;
        }

        public void setPricing(String pricing) {
            this.pricing = pricing;
        }

        public String getPricingBatch() {
            return pricingBatch;
        }

        public void setPricingBatch(String pricingBatch) {
            this.pricingBatch = pricingBatch;
        }

        public String getPricedProducts() {
            return pricedProducts;
        }

        public void setPricedProducts(String pricedProducts) {
            this.pricedProducts = pricedProducts;
        }

        public String getExportFile() {
            return exportFile;
        }

        public void setExportFile(String exportFile) {
            this.exportFile = exportFile;
        }
    }

    static class PricedSheet {
        // 根据报价表编号获取产品报价表信息
        private String sheets;
        // 修改报价产品报价表详情信息
        private String update;
        // 创建报价产品报价表详情信息
        private String create;
        // 导入产品报价表信息
        private String imported;
        // 使用file文件导入产品报价表信息
        private String importFile;
        // 导出报价表信息
        private String exportFile;

        public String getSheets() {
            return sheets;
        }

        public void setSheets(String sheets) {
            this.sheets = sheets;
        }

        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getImported() {
            return imported;
        }

        public void setImported(String imported) {
            this.imported = imported;
        }

        public String getImportFile() {
            return importFile;
        }

        public void setImportFile(String importFile) {
            this.importFile = importFile;
        }

        public String getExportFile() {
            return exportFile;
        }

        public void setExportFile(String exportFile) {
            this.exportFile = exportFile;
        }
    }
}
