package com.szmsd.system.domain.vo;

/**
 *
 * @ClassName:NameValueAttributeVo
 * @Description:键值对类
 * @author GaoJunWen
 * @date 2020-04-22
 * @version V1.0
 */
public class NameValueAttributeVo {
    private static final long serialVersionUID = 20160322001L;
    private String name;
    private Object value;

    public NameValueAttributeVo() {
    }

    public NameValueAttributeVo(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
