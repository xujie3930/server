package com.szmsd.common.core.web.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Entity基类
 *
 * @author szmsd
 */
@Data
public class BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 搜索值 */
//    private String searchValue;

    /** 创建者 */
    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private String createByName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty(value = "更新者")
    @TableField(fill = FieldFill.UPDATE)
    private String updateByName;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 数据权限 */
    @ApiModelProperty(value = "数据权限")
    @TableField(exist = false)
    private String dataScope;

    /** 开始时间 */
    @JsonIgnore
    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String beginTime;

    /** 结束时间 */
    @JsonIgnore
    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private String endTime;

    /** 请求参数 */
    @ApiModelProperty(value = "请求参数",hidden = true)
    @TableField(exist = false)
    private Map<String, Object> params=new HashMap<>();




 /*   public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
}

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }*/
}
