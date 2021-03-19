package com.szmsd.finance.controller;

import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author liulei
 */
public class FssBaseController extends BaseController {

    public String getLoginUserName(){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(ObjectUtils.isEmpty(loginUser)){
            return "";
        }
        return loginUser.getUsername();
    }

    public LoginUser getLoginUser(){
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(ObjectUtils.isEmpty(loginUser)){
            return null;
        }
        return loginUser;
    }
}
