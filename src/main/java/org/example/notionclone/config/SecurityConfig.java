package org.example.notionclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.requiresChannel().anyRequest().requiresSecure();

        // configure url authorization rules
        http.authorizeHttpRequests().requestMatchers(
                        mvc.pattern("/"),
                        mvc.pattern("/register"),
                        mvc.pattern("/notionclone/webauth/register/start"),
                        mvc.pattern("/notionclone/webauth/register/finish"),
                        mvc.pattern("/error"))
                .permitAll()
                .requestMatchers("/images/**", "/fonts/**", "/css/**", "/js/**")
                .permitAll()
                .anyRequest().authenticated();


        return http.build();
    }
}
