package com.szmsd.http.dto;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 14:19
 */
public interface ResponseObject<S, E> extends Serializable {

    /**
     * 是否成功
     *
     * @return boolean
     */
    boolean isSuccess();

    /**
     * 返回成功对象
     *
     * @return S
     */
    S getObject();

    /**
     * 返回失败对象
     *
     * @return E
     */
    E getError();

    class ResponseObjectWrapper<S, E> implements ResponseObject<S, E> {
        private boolean success;
        private S object;
        private E error;

        public ResponseObjectWrapper() {
        }

        public ResponseObjectWrapper(boolean success) {
            this.success = success;
        }

        public ResponseObjectWrapper(boolean success, S object) {
            this.success = success;
            this.object = object;
        }

        public ResponseObjectWrapper(boolean success, S object, E error) {
            this.success = success;
            this.object = object;
            this.error = error;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        @Override
        public S getObject() {
            return object;
        }

        public void setObject(S object) {
            this.object = object;
        }

        @Override
        public E getError() {
            return error;
        }

        public void setError(E error) {
            this.error = error;
        }
    }
}
