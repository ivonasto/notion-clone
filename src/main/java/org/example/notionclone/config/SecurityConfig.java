package org.example.notionclone.config;

import org.example.notionclone.authentication.login.FidoAuthenticationConverter;
import org.example.notionclone.authentication.login.FidoAuthenticationManager;
import org.example.notionclone.authentication.login.FidoLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {


    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc,FidoAuthenticationManager fidoAuthenticationManager) throws Exception {
        http.requiresChannel().anyRequest().requiresSecure();

        http.headers().frameOptions().disable();

        // configure url authorization rules
        http.authorizeHttpRequests().requestMatchers(
                        mvc.pattern("/"),
                        mvc.pattern("/login"),
                        mvc.pattern("/register"),
                        mvc.pattern("/notionclone/webauth/register/start"),
                        mvc.pattern("/notionclone/webauth/register/finish"),
                        mvc.pattern("/notionclone/webauth/login/start"),
                        mvc.pattern("/error"))
                .permitAll()
                .requestMatchers("/images/**", "/fonts/**", "/css/**", "/js/**")
                .permitAll()
                .anyRequest().authenticated();


        var authenticationFilter = new AuthenticationFilter(fidoAuthenticationManager,new FidoAuthenticationConverter());
        authenticationFilter.setRequestMatcher(new AntPathRequestMatcher("/notionclone/webauth/login/finish"));
        authenticationFilter.setSuccessHandler(new FidoLoginSuccessHandler());
        authenticationFilter.setSecurityContextRepository( new HttpSessionSecurityContextRepository());
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
