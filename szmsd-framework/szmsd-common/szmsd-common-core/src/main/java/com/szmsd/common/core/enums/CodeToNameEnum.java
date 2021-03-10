package com.szmsd.common.core.enums;


import com.szmsd.common.core.utils.StringUtils;

/**
 * @Author: lyf
 **/
public enum CodeToNameEnum {
    BAS_SUB("bas_sub", "主子类别"),
    BAS_EMPLOYEE("bas_employee", "业务员"),
    BAS_CUSTOMER("bas_customer", "客户"),
    BAS_PRODUCT("bas_product_type", "产品类型"),
    BAS_COUNTRY("bas_country", "国家"),
    BAS_PROVINCE("bas_province", "省份"),
    BAS_CITY("bas_city", "城市"),
    BAS_WAREHOUSE("bas_warehouse", "仓库信息"),
    ;
    private String type;
    private String value;

    CodeToNameEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String remark) {
        this.value = remark;
    }

    public String getValueByType(String type) {
        if (StringUtils.isNotNull(type)) {
            for (CodeToNameEnum codeToNameEnum : CodeToNameEnum.values()) {
                if (type.equalsIgnoreCase(codeToNameEnum.getType())) {
                    return codeToNameEnum.getValue();
                }
            }
        }
        return null;
    }
}