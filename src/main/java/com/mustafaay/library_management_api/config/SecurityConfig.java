package com.mustafaay.library_management_api.config;

import com.mustafaay.library_management_api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // buradan hangi endpointe kim erişebilir kuralları
                .authorizeHttpRequests(auth -> auth

                        // Auth ve H2 serbest
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // =========================
                        // GET / listeleme-görüntüleme
                        // ADMIN ve LIBRARIAN
                        // =========================
                        .requestMatchers(HttpMethod.GET, "/api/books/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.GET, "/api/members/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.GET, "/api/authors/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.GET, "/api/categories/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.GET, "/api/loans/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.GET, "/api/fines/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        // =========================
                        // Loan/Fine operasyonları
                        // ADMIN ve LIBRARIAN
                        // =========================
                        .requestMatchers(HttpMethod.POST, "/api/loans/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.PUT, "/api/loans/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.PATCH, "/api/loans/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.DELETE, "/api/loans/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/fines/**")
                        .hasAnyRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/fines/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        .requestMatchers(HttpMethod.PATCH, "/api/fines/**")
                        .hasAnyRole("ADMIN", "LIBRARIAN")

                        // =========================
                        // Yönetimsel CRUD işlemleri
                        // sadece ADMIN
                        // =========================
                        .requestMatchers(HttpMethod.POST, "/api/books/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/books/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/books/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/books/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/members/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/members/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/members/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/members/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/authors/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/authors/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/authors/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/authors/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/categories/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/categories/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/categories/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                        .hasRole("ADMIN")

                        // Diğer tüm istekler login ister
                        .anyRequest().authenticated()
                )

                //yetki yetersi olursa yazdığım customAccessDeniedHandler çalışcak
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
// kullanıcı login olurken  - bu email/şifre doğru mu?
    @Bean
    public AuthenticationProvider authenticationProvider() {
        //kullanıcı bilgisini veritabanından çeken provider
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        //veritabanında kullanıcı şifresi düz metinde şeklinde tutulmaz. kullanıcının girdiği düz şifreyi veritabanındaki hashli
        //şifreyle BCrypt kullanarak karşılaştırır.
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    //kullanolacak şifreleme algoritmasını burada tanımladım.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}