package com.szmsd.doc.api.delivery.request.swagger.filter;

import com.szmsd.doc.api.SwaggerDictionary;

import java.util.List;

public class OrderTypeDataFilter implements SwaggerDictionary.DataFilter {

    @Override
    public boolean filter(SwaggerDictionary.DataFormat dataFormat) {
        if ("NewSku".equals(dataFormat.getKey())
                || "SplitSku".equals(dataFormat.getKey())) {
            return false;
        }
        return true;
    }

    @Override
    public List<SwaggerDictionary.DataFormat> filter(List<SwaggerDictionary.DataFormat> dataFormatList) {
        return dataFormatList;
    }
}
