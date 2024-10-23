package com.daelim.sfa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable()) //no cache 비활성화
                        /*
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self' https://stackpath.bootstrapcdn.com https://cdn.jsdelivr.net https://fonts.googleapis.com " +
                                        "https://fonts.gstatic.com https://cdnjs.cloudflare.com https://cdn.iamport.kr https://service.iamport.kr https://online-pay.kakao.com " +
                                        "https://dapi.kakao.com https://t1.daumcdn.net https://map.daumcdn.net")
                        )

                         */
                )
                //.cors(AbstractHttpConfigurer::disable)
                .csrf((csrf) -> csrf.disable());
        return http.build();
    }



    /*
    @Autowired
    public void configure(AuthenticationManagerBuilder builder) {
        builder.eraseCredentials(false);
    }

     */



}
