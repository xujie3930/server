package com.szmsd.common.core.utils;

import org.apache.http.Header;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 13:54
 */
public interface HttpResponseBody {

    /**
     * 状态码
     *
     * @return int
     */
    int getStatus();

    /**
     * 返回数据
     *
     * @return String
     */
    String getBody();

    class HttpResponseBodyEmpty implements HttpResponseBody {

        @Override
        public int getStatus() {
            return 0;
        }

        @Override
        public String getBody() {
            return "";
        }
    }

    class HttpResponseBodyWrapper implements HttpResponseBody {
        private int status;
        private String body;

        public HttpResponseBodyWrapper() {
        }

        public HttpResponseBodyWrapper(int status) {
            this.status = status;
        }

        public HttpResponseBodyWrapper(int status, String body) {
            this.status = status;
            this.body = body;
        }

        @Override
        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    class HttpResponseByteArrayWrapper implements HttpResponseBody {
        private int status;
        private Header[] headers;
        private byte[] byteArray;

        public HttpResponseByteArrayWrapper() {
        }

        public HttpResponseByteArrayWrapper(int status, Header[] headers, byte[] byteArray) {
            this.status = status;
            this.headers = headers;
            this.byteArray = byteArray;
        }

        @Override
        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String getBody() {
            return null;
        }

        public Header[] getHeaders() {
            return headers;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }

        public byte[] getByteArray() {
            return byteArray;
        }

        public void setByteArray(byte[] byteArray) {
            this.byteArray = byteArray;
        }
    }
}
