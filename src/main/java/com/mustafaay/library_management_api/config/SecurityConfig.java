package com.mustafaay.library_management_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Şimdilik CSRF kapalı. H2 Console ve test istekleri rahat çalışsın diye.
                .csrf(csrf -> csrf.disable())

                // H2 Console iframe kullandığı için frame ayarını kapatıyoruz.
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // Şimdilik tüm endpointleri serbest bırakıyoruz.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}