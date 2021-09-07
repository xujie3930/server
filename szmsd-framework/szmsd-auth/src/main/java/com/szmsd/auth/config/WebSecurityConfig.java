package com.szmsd.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Security 安全认证相关配置
 * Oauth2依赖于Security 默认情况下WebSecurityConfig执行比ResourceServerConfig优先
 *
 * @author szmsd
 */
@Order(99)
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private UserDetailsService userDetailsService;
    @Autowired
    private DocCodeAuthConfiguration docCodeAuthConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter = new UsernamePasswordAuthenticationFilter();
        usernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);
        return usernamePasswordAuthenticationFilter;
    }

    @Bean
    public DocDefaultLoginPageGeneratingFilter docDefaultLoginPageGeneratingFilter(UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter) {
        DocDefaultLoginPageGeneratingFilter loginPageGeneratingFilter = new DocDefaultLoginPageGeneratingFilter(usernamePasswordAuthenticationFilter);
        loginPageGeneratingFilter.setFormLoginEnabled(true);
        loginPageGeneratingFilter.setUsernameParameter(usernamePasswordAuthenticationFilter.getUsernameParameter());
        loginPageGeneratingFilter.setPasswordParameter(usernamePasswordAuthenticationFilter.getPasswordParameter());
        loginPageGeneratingFilter.setLoginPageUrl("/login");
        loginPageGeneratingFilter.setFailureUrl("/login?error");
        loginPageGeneratingFilter.setAuthenticationUrl(this.docCodeAuthConfiguration.getAuthenticationUrl());
        return loginPageGeneratingFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/actuator/**", "/oauth/*", "/token/**",
                        "/login",
                        this.docCodeAuthConfiguration.getAuthenticationUrl()
                )
                .permitAll()

                .anyRequest()
                .permitAll()

                .and().formLogin()
                .loginPage(this.docCodeAuthConfiguration.getLocationUrl())
                // .loginPage("http://127.0.0.1:8080/auth/login")

                .and().logout();
    }
}
