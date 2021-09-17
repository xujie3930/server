package com.szmsd.doc.validator;

/**
 * 数据验证
 */
public interface DictionaryPlugin {

    /**
     * valid
     *
     * @param value value
     * @param param param
     * @return boolean
     */
    boolean valid(Object value, String param);
}
