package com.roadmap.config;

import com.roadmap.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    private final UserDetailsService accountService;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/","/login","/h2-console/**","/sign-up","/popup/jusoPopup","/sign-up","/email-login").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();
        http.csrf().ignoringAntMatchers("/popup/jusoPopup","/settings/location");
        http.formLogin().loginPage("/login").permitAll();
        //http.logout().deleteCookies("remember-me","JSESSION_ID").logoutSuccessUrl("/");
        http.logout().deleteCookies("JSESSION_ID").logoutSuccessUrl("/");
        http.rememberMe()
                .tokenValiditySeconds(60*60*7)
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository());
        http.oauth2Login().loginPage("/login").successHandler(successHandler());
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        log.info("passwordEncoder : " + passwordEncoder);
        return new LoginSuccessHandler(passwordEncoder);
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/node_modules/**","/**/*.js","/**/*.css","/images/**","/static/js/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}