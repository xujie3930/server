package com.szmsd.http.config.inner.api;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:00
 */
public class SaaSProductRemoteAreaApiConfig implements ApiConfig {

    /**
     * RemoteAreaTemplate
     */
    private RemoteAreaTemplate remoteAreaTemplate;

    public RemoteAreaTemplate getRemoteAreaTemplate() {
        return remoteAreaTemplate;
    }

    public void setRemoteAreaTemplate(RemoteAreaTemplate remoteAreaTemplate) {
        this.remoteAreaTemplate = remoteAreaTemplate;
    }

    static class RemoteAreaTemplate {
        // 分页查询地址库模板列表，返回指定页面的数据，以及统计总记录数
        private String pageResult;
        // 根据模板id获取地址库模板信息
        private String get;
        // 导出地址库模板信息
        private String exportFile;
        // 导入地址库模板
        private String importFile;
        // 判断是否为住宅地址和偏远地址
        private String remoteTemplateRule;

        public String getPageResult() {
            return pageResult;
        }

        public void setPageResult(String pageResult) {
            this.pageResult = pageResult;
        }

        public String getGet() {
            return get;
        }

        public void setGet(String get) {
            this.get = get;
        }

        public String getExportFile() {
            return exportFile;
        }

        public void setExportFile(String exportFile) {
            this.exportFile = exportFile;
        }

        public String getImportFile() {
            return importFile;
        }

        public void setImportFile(String importFile) {
            this.importFile = importFile;
        }

        public String getRemoteTemplateRule() {
            return remoteTemplateRule;
        }

        public void setRemoteTemplateRule(String remoteTemplateRule) {
            this.remoteTemplateRule = remoteTemplateRule;
        }
    }
}
