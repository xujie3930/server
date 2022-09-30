package com.szmsd.doc.api.auth;

import cn.hutool.core.codec.Base64Decoder;
import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.constant.SecurityConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.UserStatus;
import com.szmsd.common.core.exception.com.LogisticsException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import com.szmsd.system.api.feign.AuthClientService;
import com.szmsd.system.api.feign.RemoteUserService;
import com.szmsd.system.api.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @Author lc
 * @Date 2022/9/1 9:35
 * @PackageName:com.szmsd.doc.controller
 * @ClassName: BasicOauthController
 * @Description: 第三方basic授权
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/authorization")
public class BasicOauthController {
    @Resource
    private AuthClientService authClient;

    @Resource
    private RemoteUserService remoteUserService;

    @GetMapping("/basicAuthorization")
    public R<Object> basicAuthorization(HttpServletRequest request) {
        log.info("[wms-business-doc]-[BasicOauthController]-[basicAuthorization] 接收到的 params:{}", JSON.toJSONString(request.getParameterMap()));
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder sb = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            sb.append(headerName).append(":").append(request.getHeader(headerName));
        }
        log.info("[wms-business-doc]-[BasicOauthController]-[basicAuthorization] 接收到的 header:{}", sb.toString());

        String authorStr = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorStr)) {
            throw new LogisticsException("500", "授权参数为空");
        }
        try {
            String[] sp1 = authorStr.split(" ");
            String authorHeader = sp1[0];
            if (!"basic".equalsIgnoreCase(authorHeader)) {
                throw new LogisticsException("500", "授权参数有误");
            }
            String authorContent = sp1[1];
            authorContent = Base64Decoder.decodeStr(authorContent);
            String[] sp2 = authorContent.split(":");
            String username = sp2[0];
            String password = sp2[1];
            log.info("[wms-business-doc]-[BasicOauthController]-[basicAuthorization] 解析后的username:{},password:{}", username, password);
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                throw new LogisticsException("500", "授权参数有误");
            }
            SysUserByTypeAndUserType sysUserByTypeAndUserType = new SysUserByTypeAndUserType();
            sysUserByTypeAndUserType.setType(SecurityConstants.DETAILS_TYPE_CLIENT);
            sysUserByTypeAndUserType.setUsername(username);
            sysUserByTypeAndUserType.setUserType("01");
            log.info("[wms-business-doc]-[BasicOauthController]-[basicAuthorization] 查询用户信息 请求参数:{}", JSON.toJSONString(sysUserByTypeAndUserType));
            R<UserInfo> userResult = remoteUserService.getUserInfo(sysUserByTypeAndUserType);
            log.info("[wms-business-doc]-[BasicOauthController]-[basicAuthorization] 查询用户信息 响应结果:{}", JSON.toJSONString(userResult));
            if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData()) || StringUtils.isNull(userResult.getData().getSysUser())) {
                throw new LogisticsException("500", "授权信息不存在");
            } else if (UserStatus.DELETED.getCode().equals(userResult.getData().getSysUser().getDelFlag())) {
                throw new LogisticsException("500", "授权信息已被删除");
            } else if (UserStatus.DISABLE.getCode().equals(userResult.getData().getSysUser().getStatus())) {
                throw new LogisticsException("500", "授权信息已被停用");
            }

            //匹配秘钥
            UserInfo userInfo = userResult.getData();
            SysUser sysUser = userInfo.getSysUser();
            String sellerKey = sysUser.getSellerKey();
            if (StringUtils.isEmpty(sellerKey)) {
                throw new LogisticsException("500", "授权信息未维护");
            }
            if (!sellerKey.equals(password)) {
                throw new LogisticsException("500", "授权信息不正确");
            }

            log.info("[wms-business-doc]-[BasicOauthController]-[basicAuthorization] 内部登录 请求参数:{}", JSON.toJSONString(username));
            Object data = authClient.token(username, "1", "01", "client", "password", "123456", SecurityConstants.LOGIN_FREE, "Basic Y2xpZW50OjEyMzQ1Ng==");
            return R.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof LogisticsException) {
                throw new LogisticsException("500", e.getMessage());
            }
            log.info("[wms-business-doc]-[BasicOauthController]-[basicAuthorization] 出现异常,异常params:{},异常header:{}", JSON.toJSONString(request.getParameterMap()), sb.toString(), e);
            throw new LogisticsException("500", "授权参数格式有误");
        }
    }
}
