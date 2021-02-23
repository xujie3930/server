package com.szmsd.system.api.factory;

import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.domain.dto.SysUserByTypeAndUserType;
import com.szmsd.system.api.domain.dto.SysUserDto;
import com.szmsd.system.api.feign.RemoteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.model.UserInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户服务降级处理
 *
 * @author szmsd
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService() {


            @Override
            public R<SysUser> queryGetInfoByUserId(Long userId) {
                return null;
            }

            @Override
            public R<UserInfo> getUserInfo(SysUserByTypeAndUserType sysUserByTypeAndUserType) {

                return null;
            }

            @Override
            public R baseCopyUserAdd(SysUserDto userDto) {
                return null;
            }

            @Override
            public R baseCopyUserEdit(SysUserDto userDto) {
                return null;
            }

            @Override
            public R remove(Long userId) {
                return null;
            }

            @Override
            public R delFlag(Long[] userIds) {
                return null;
            }

            @Override
            public R changeStatus(SysUserDto userDto) {
                return null;
            }
        };
    }
}
