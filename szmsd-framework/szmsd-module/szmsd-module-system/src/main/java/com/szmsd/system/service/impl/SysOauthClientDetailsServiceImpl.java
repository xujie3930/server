package com.szmsd.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.system.domain.SysOauthClientDetails;
import com.szmsd.system.mapper.SysOauthClientDetailsMapper;
import com.szmsd.system.service.ISysOauthClientDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SysOauthClientDetailsServiceImpl extends ServiceImpl<SysOauthClientDetailsMapper, SysOauthClientDetails> implements ISysOauthClientDetailsService {
}
