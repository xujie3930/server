package com.szmsd.http.config.inner.api;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:06
 */
public class ThirdPaymentApiConfig implements ApiConfig {

    /**
     * Recharges
     */
    private Recharges recharges;

    /**
     * Callback
     */
    private Callback callback;

    public Recharges getRecharges() {
        return recharges;
    }

    public void setRecharges(Recharges recharges) {
        this.recharges = recharges;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    static class Recharges {
        // 线上充值请求接口
        private String recharges;

        public String getRecharges() {
            return recharges;
        }

        public void setRecharges(String recharges) {
            this.recharges = recharges;
        }
    }

    static class Callback {
        // 回调通知地址
        private String notifyUrl;

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public void setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
        }
    }
}
