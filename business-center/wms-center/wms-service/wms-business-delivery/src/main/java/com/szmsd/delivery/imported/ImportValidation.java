package com.szmsd.delivery.imported;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 17:08
 */
public interface ImportValidation<T> {

    @SafeVarargs
    static <T> List<ImportValidation<T>> build(ImportValidation<T>... validations) {
        return new ArrayList<>(Arrays.asList(validations));
    }

    void valid(int rowIndex, T object);
}
