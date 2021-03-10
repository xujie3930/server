package com.szmsd.common.swagger.config;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 17:32
 */
public class SwaggerHeaderProperties implements Serializable {

    private String name;
    private String description;
    private String defaultValue;
    private boolean required;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
