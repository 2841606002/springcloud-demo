package com.example.uaa.config;

import com.example.uaa.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * 配置OOS认证方式
     *
     * @param auth AuthenticationManagerBuilder
     * @throws Exception 异常
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 配置数据库认证
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        // 配置 LDAP 认证
        auth.ldapAuthentication()
                .userDnPatterns("uid={0},ou=people") // 用户 DN 模式
                .groupSearchBase("ou=groups") // 组搜索基础
                .contextSource()
                .url("ldap://localhost:389/dc=example,dc=com") // LDAP 服务器地址
                .and()
                .passwordCompare()
                .passwordEncoder(passwordEncoder())
                .passwordAttribute("userPassword"); // 密码属性
    }

    /**
     * 配置 URL 权限控制
     *
     * @param http HttpSecurity
     * @throws Exception 异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll() // 允许访问认证接口
                .antMatchers("/oauth2/**").permitAll() // 允许访问 OAuth2 接口
                .anyRequest().authenticated() // 其他请求需要认证
                .and()
                .oauth2Login() // 启用 OAuth2 登录
                .defaultSuccessUrl("/auth/oauth2/github"); // OAuth2 登录成功后的回调地址
    }

    /**
     * 配置密码编码器
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 使用 BCrypt 加密
    }

    /**
     * 暴露 AuthenticationManager Bean
     *
     * @return AuthenticationManager
     * @throws Exception 异常
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}