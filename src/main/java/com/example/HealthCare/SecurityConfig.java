package com.example.HealthCare;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disables CSRF so your forms work easily
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allows everyone to see all pages
                )
                .formLogin(form -> form.disable()) // Specifically turns off that login page
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}