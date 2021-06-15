package com.szmsd.common.security.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.feign.RemoteUserService;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;


/**
 * 自动填充补充类
 * 只用于mybatis-plus @TableField 注解的
 * 遗留问题：ByName 字段
 * //todo 待补充非 mybatis-plus dml语句，放入公共模块下
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //11-9 补充表未设置默认时间
    private final String createTime = "createTime";
    private final String createBy = "createBy";
    private final String createByName = "createByName";
    private final String updateTime = "updateTime";
    private final String updateBy = "updateBy";
    private final String updateByName = "updateByName";
    private final String version = "version";

    @Resource
    private RemoteUserService remoteUserService;

    @Override
    public void insertFill(MetaObject metaObject) {
        if (valid(createTime, metaObject)) {
            this.setFieldValByName(createTime, new Date(), metaObject);
        }
        if (valid(createBy, metaObject)) {
            this.setFieldValByName(createBy, StringUtils.isEmpty(getUser().getUserName()) ? "admin" : getUser().getUserName(), metaObject);
        }
        if (valid(createByName, metaObject)) {
            this.setFieldValByName(createByName, StringUtils.isEmpty(getUser().getNickName()) ? "系统" : getUser().getNickName(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (valid(updateTime, metaObject)) {
            this.setFieldValByName(updateTime, new Date(), metaObject);
        }
        if (valid(updateBy, metaObject)) {
            this.setFieldValByName(updateBy, StringUtils.isEmpty(getUser().getUserName()) ? "admin" : getUser().getUserName(), metaObject);
        }
        if (valid(updateByName, metaObject)) {
            this.setFieldValByName(updateByName, StringUtils.isEmpty(getUser().getNickName()) ? "系统" : getUser().getNickName(), metaObject);
        }
    }

    public SysUser getUser() {
        SysUser user = new SysUser();
        if (null != SecurityUtils.getLoginUser()) {
            R<SysUser> sysUserR = remoteUserService.queryGetInfoByUserId(SecurityUtils.getLoginUser().getUserId());
            if (null != sysUserR && 200 == sysUserR.getCode() && null != sysUserR.getData()) {
                user = sysUserR.getData();
            }
        }
        return user;
    }

    /**
     * 验证
     *
     * @param name
     * @param mataObejct
     * @return
     */
    private boolean valid(String name, MetaObject mataObejct) {
        return (mataObejct.hasGetter(name) && this.getFieldValByName(name, mataObejct) == null) ? true : false;
    }
}