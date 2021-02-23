//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.szmsd.system.api.domain;

import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;


/**
 *
 * @ClassName:SysLang
 * @Description:
 * @author GaoJunWen
 * @date 2020-04-22
 * @version V1.0
 */
public class SysLang extends BaseEntity {

    /***
     * 编号 唯一索引
     */
    @ApiModelProperty(value = "编号 唯一索引")
    private String code;
    /**
     * 所属分组类型
     */
    @ApiModelProperty(value = "所属分组类型")
    private int groupType;
    /**
     * 中文
     */
    @ApiModelProperty(value = "中文")
    private String strId;
    /**
     * 语言1
     */
    @ApiModelProperty(value = "语言1")
    private String lang1;
    /**
     * 语言2
     */
    @ApiModelProperty(value = "语言2")
    private String lang2;
    /**
     * 语言3
     */
    @ApiModelProperty(value = "语言3")
    private String lang3;
    /**
     * 语言4
     */
    @ApiModelProperty(value = "语言4")
    private String lang4;
    /**
     * 语言5
     */
    @ApiModelProperty(value = "语言5")
    private String lang5;
    /**
     * 是否显示
     */
    @ApiModelProperty(value = "是否显示 默认1")
    private Integer visible;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public String getLang1() {
        return lang1;
    }

    public void setLang1(String lang1) {
        this.lang1 = lang1;
    }

    public String getLang2() {
        return lang2;
    }

    public void setLang2(String lang2) {
        this.lang2 = lang2;
    }

    public String getLang3() {
        return lang3;
    }

    public void setLang3(String lang3) {
        this.lang3 = lang3;
    }

    public String getLang4() {
        return lang4;
    }

    public void setLang4(String lang4) {
        this.lang4 = lang4;
    }

    public String getLang5() {
        return lang5;
    }

    public void setLang5(String lang5) {
        this.lang5 = lang5;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }
}
