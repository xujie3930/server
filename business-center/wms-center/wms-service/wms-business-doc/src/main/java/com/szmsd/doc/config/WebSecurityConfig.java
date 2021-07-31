package com.szmsd.doc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 09:25
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final DocOauthConfig docOauthConfig;

    public WebSecurityConfig(DocOauthConfig docOauthConfig) {
        this.docOauthConfig = docOauthConfig;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 必须注入 AuthenticationManager，不然oauth无法处理四种授权方式
     *
     * @return AuthenticationManager
     * @throws Exception Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()

                .and()
                .formLogin()

                .and()
                .logout();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**",
                "/swagger-resources/**",
                "/v1/**",
                "/v2/**",
                "/doc.html",
                "/swagger-ui.html",
                "/api/test/token");
    }

    /**
     * 注入UserDetailsService
     *
     * @return UserDetailsService
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        List<UserConfig> users = docOauthConfig.getUsers();
        for (UserConfig user : users) {
            userDetailsManager.createUser(User.withUsername(user.getName()).password(passwordEncoder().encode(user.getPassword())).authorities(user.getAuth()).build());
        }
        return userDetailsManager;
    }
}
