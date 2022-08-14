package com.twitter.elastic.query.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserConfigData userDataConfig;

    public WebSecurityConfig(UserConfigData userDataConfig) {
        this.userDataConfig = userDataConfig;
    }

    @Override
    public void configure(HttpSecurity security) throws Exception {
        security.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/**").hasRole("USER")
                .and()
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(userDataConfig.getUsername())
                .password(passwordEncoder().encode(userDataConfig.getPass()))
                .roles(userDataConfig.getRoles());
    }

    @Bean
    protected PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
