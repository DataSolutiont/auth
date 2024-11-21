package com.mreblan.auth.config;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.mreblan.auth.security.JwtTokenFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // private UserServiceImpl userService;
    private JwtTokenFilter tokenFilter;

    public SecurityConfig() {}

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void setTokenFilter(JwtTokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(httpSecurityConfigurer -> 
                    httpSecurityConfigurer.configurationSource(request -> 
                        new CorsConfiguration().applyPermitDefaultValues()
                )        
            )
            .exceptionHandling(exceptions -> exceptions
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))        
            )
            .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests((requests) -> requests
                                            .requestMatchers(
                                                "/api/auth/**",
                                                "/swagger-ui.html",
                                                "swagger-ui/**",
                                                "/v3/**",
                                                // "/test/**",
                                                "/api-docs"
                                            ).permitAll()
                                            .anyRequest().authenticated()
                                )
            .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
} 
