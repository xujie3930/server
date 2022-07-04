package com.szmsd.http.config.inner.api;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 13:50
 */
public class SaaSPricedApiConfig implements ApiConfig {

    public PricedGrade getPricedGrade() {
        return pricedGrade;
    }

    public void setPricedGrade(PricedGrade pricedGrade) {
        this.pricedGrade = pricedGrade;
    }

    /**
     * PricedGrade
     */
    private PricedGrade pricedGrade;

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    private Grade grade;


    private Discount discount;

    private CustomPrices customPrices;

    public CustomPrices getCustomPrices() {
        return customPrices;
    }

    public void setCustomPrices(CustomPrices customPrices) {
        this.customPrices = customPrices;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    static class PricedGrade {
        // 获取单条
        private String get;
        // 创建报价等级信息
        private String create;
        // 分页查询报价等级列表，返回指定页面的数据，以及统计总记录数
        private String pageResult;
        // 修改报价等级信息
        private String update;
        // 删除报价等级信息
        private String delete;

        private String list;
        public String getGet() {
            return get;
        }

        public void setGet(String get) {
            this.get = get;
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

        public String getList() {
            return list;
        }

        public void setList(String list) {
            this.list = list;
        }
    }

    static class Grade {


        // 明细查询
        private String detailResult;
        private String page;
        private String detailImport;
        private String customUpdate;
        private String create;
        private String update;

        public String getDetailResult() {
            return detailResult;
        }

        public void setDetailResult(String detailResult) {
            this.detailResult = detailResult;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getDetailImport() {
            return detailImport;
        }

        public void setDetailImport(String detailImport) {
            this.detailImport = detailImport;
        }

        public String getCustomUpdate() {
            return customUpdate;
        }

        public void setCustomUpdate(String customUpdate) {
            this.customUpdate = customUpdate;
        }

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }
    }
    static class Discount {

        // 明细查询
        private String detailResult;
        private String page;
        private String detailImport;
        private String customUpdate;
        private String create;
        private String update;

        public String getDetailResult() {
            return detailResult;
        }

        public void setDetailResult(String detailResult) {
            this.detailResult = detailResult;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getDetailImport() {
            return detailImport;
        }

        public void setDetailImport(String detailImport) {
            this.detailImport = detailImport;
        }

        public String getCustomUpdate() {
            return customUpdate;
        }

        public void setCustomUpdate(String customUpdate) {
            this.customUpdate = customUpdate;
        }

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getUpdate() {
            return update;
        }

        public void setUpdate(String update) {
            this.update = update;
        }
    }
    static class CustomPrices {

        private String page;
        private String updateDiscount;
        private String updateGrade;
        private String updateDetailDiscount;
        private String updateDetailGrade;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getUpdateDiscount() {
            return updateDiscount;
        }

        public void setUpdateDiscount(String updateDiscount) {
            this.updateDiscount = updateDiscount;
        }

        public String getUpdateGrade() {
            return updateGrade;
        }

        public void setUpdateGrade(String updateGrade) {
            this.updateGrade = updateGrade;
        }

        public String getUpdateDetailDiscount() {
            return updateDetailDiscount;
        }

        public void setUpdateDetailDiscount(String updateDetailDiscount) {
            this.updateDetailDiscount = updateDetailDiscount;
        }

        public String getUpdateDetailGrade() {
            return updateDetailGrade;
        }

        public void setUpdateDetailGrade(String updateDetailGrade) {
            this.updateDetailGrade = updateDetailGrade;
        }
    }

}
