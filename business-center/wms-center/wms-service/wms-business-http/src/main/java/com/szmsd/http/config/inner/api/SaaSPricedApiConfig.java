package com.szmsd.http.config.inner.api;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 13:50
 */
public class SaaSPricedApiConfig implements ApiConfig {

    public PricedGrade2 getPricedGrade() {
        return pricedGrade;
    }

    public void setPricedGrade(PricedGrade2 pricedGrade) {
        this.pricedGrade = pricedGrade;
    }

    /**
     * PricedGrade
     */
    private PricedGrade2 pricedGrade;



    static class PricedGrade2 {
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

    }

}
