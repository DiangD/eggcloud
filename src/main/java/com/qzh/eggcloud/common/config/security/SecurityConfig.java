package com.qzh.eggcloud.common.config.security;

import com.qzh.eggcloud.auth.evaluator.SysUserPermissionEvaluator;
import com.qzh.eggcloud.auth.filter.JWTAuthenticationTokenFilter;
import com.qzh.eggcloud.auth.handler.*;
import com.qzh.eggcloud.auth.provider.UserAuthenticationProvider;
import com.qzh.eggcloud.auth.service.SysUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * @ClassName SecurityConfig
 * @Author DiangD
 * @Date 2021/3/5
 * @Version 1.0
 * @Description web security configuration
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 自定义登录成功处理器
     */
    @Autowired
    private UserLoginSuccessHandler userLoginSuccessHandler;

    /**
     * 自定义登录失败处理器
     */
    @Autowired
    private UserLoginFailureHandler userLoginFailureHandler;

    /**
     * 自定义注销成功处理器
     */
    @Autowired
    private UserLogoutSuccessHandler userLogoutSuccessHandler;

    /**
     * 自定义暂无权限处理器
     */
    @Autowired
    private UserAuthAccessDeniedHandler userAuthAccessDeniedHandler;

    /**
     * 自定义登录逻辑验证器
     */
    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private SysUserDetailService userDetailService;

    @Autowired
    private PersistentTokenRepository repository;


    /**
     * 自定义未登录的处理器
     */
    @Autowired
    private UserAuthenticationEntryPointHandler userAuthenticationEntryPointHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 注入自定义PermissionEvaluator
     */
    @Bean
    public DefaultWebSecurityExpressionHandler userSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(new SysUserPermissionEvaluator());
        return handler;
    }

    /**
     * 配置登录验证逻辑
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里可启用我们自己的登陆验证逻辑
        auth.authenticationProvider(userAuthenticationProvider);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(JWTConfig.antMatchers.split(","))
                .permitAll()
                .antMatchers(HttpMethod.HEAD).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic().authenticationEntryPoint(userAuthenticationEntryPointHandler)
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .usernameParameter("loginKey")
                .passwordParameter("password")
                .successHandler(userLoginSuccessHandler)
                .failureHandler(userLoginFailureHandler)
                .and()
                .rememberMe()
                .key("rememberMe")
                .rememberMeParameter("rememberMe")
                .userDetailsService(userDetailService)
                .tokenRepository(repository)
                .and()
                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .logoutSuccessHandler(userLogoutSuccessHandler)
                .deleteCookies("remember-me", "eggcloud-token", "userId")
                .and()
                .exceptionHandling().accessDeniedHandler(userAuthAccessDeniedHandler)
                .and()
                .cors()
                .and()
                .csrf()
                .disable();
        //基于token不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //禁用缓存
        http.headers().cacheControl().disable();
        //添加jwt拦截器
        http.addFilter(new JWTAuthenticationTokenFilter(authenticationManager()));
    }
}
