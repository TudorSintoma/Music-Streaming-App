package com.tudor.demo.config;

import com.tudor.demo.utils.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/token",
                                "/create-account",
                                "/add-account-type",
                                "/api/password-reset/**",
                                "/users/**",
                                "/genres/**",
                                "/update-genre",
                                "/listeners/**",
                                "/artists/**",
                                "/artist-influences"
                        ).permitAll()

                        // Listener-only access
                        .requestMatchers(
                                "/likes/**",
                                "/follows/**",
                                "/playlists/**",
                                "/playlist-songs/**"
                        ).hasRole("listener")

                        // Shared between both roles
                        .requestMatchers(
                                "/songs/**",
                                "/albums/**"
                        ).hasAnyRole("listener", "artist")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}