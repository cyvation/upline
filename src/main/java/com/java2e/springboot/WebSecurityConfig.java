package com.java2e.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @describe:
 * @author:liangcan
 * @date: 2017-12-12 16:12
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${spring.profiles}")
    private String env;
    @Value("${login.username}")
    private String username;
    @Value("${login.password}")
    private String password;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /*if("dev".equals(env)){ //如果需要在开发服中免登录
            http.authorizeRequests().antMatchers("*//**","*//**//*filters").permitAll();
            http.csrf().disable();
            http.httpBasic();
            return;
        }*/
        http.headers().frameOptions().disable();
        http.authorizeRequests()
                .antMatchers("/**/*.css").permitAll()
                .antMatchers("/**/*.js").permitAll()
                .antMatchers("/**/*.png").permitAll()
                .antMatchers("/**/*.gif").permitAll()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login")
                //设置默认登录成功跳转页面
                .defaultSuccessUrl("/upline").failureUrl("/login?error").permitAll()
                .and()
                //开启cookie保存用户数据
                .rememberMe()
                //设置cookie有效期
                .tokenValiditySeconds(60 * 60 * 24 * 7)
                //设置cookie的私钥
                .key("1231")
                .and()
                .logout()
                //默认注销行为为logout，可以通过下面的方式来修改
                .logoutUrl("/logout")
                //设置注销成功后跳转页面，默认是跳转到登录页面
                .logoutSuccessUrl("/login")
                .permitAll();
        http.csrf().disable();
        http.httpBasic();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("loginId = " + username);
        auth
                .inMemoryAuthentication()
                .withUser(username).password(password).roles("USER");
    }

}
