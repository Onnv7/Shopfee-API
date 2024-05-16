package com.hcmute.shopfee.security;

import com.hcmute.shopfee.security.custom.employee.CustomEmployeeDetailsService;
import com.hcmute.shopfee.security.custom.employee.EmployeeCustomAuthenticationProvider;
import com.hcmute.shopfee.security.custom.user.CustomUserDetailsService;
import com.hcmute.shopfee.security.custom.user.UserCustomAuthenticationProvider;
import com.hcmute.shopfee.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) //EnableGlobalMethodSecurity
public class WebSecurityConfig {

    private final EmployeeCustomAuthenticationProvider employeeCustomAuthenticationProvider;
    private final UserCustomAuthenticationProvider userCustomAuthenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(7);
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .parentAuthenticationManager(null)
                .authenticationProvider(employeeCustomAuthenticationProvider)
                .authenticationProvider(userCustomAuthenticationProvider)
                .build();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
//        configuration.addAllowedOrigin(corsAllowedOrigin); // @Value: http://localhost:8080
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://admin-shopfee.netlify.app",
                "https://shopfee.netlify.app",
                "https://shopfee.surge.sh",
                "https://admin-shopfee.surge.sh",
                "https://admin-shopfee.surge.sh",
                "https://admin-shopfee.surge.sh",
                "http://localhost:3001")); //"http://localhost:3000", "http://tender-grackle-gratefully.ngrok-free.app", "https://tender-grackle-gratefully.ngrok-free.app"
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // set route sẽ ăn từ trên xuống (ưu tiên cái đầu tiên)
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .securityMatcher("/**")
                .authenticationManager(authenticationManager(http))
                .authorizeHttpRequests(register -> register
                        .requestMatchers("/**").permitAll()
                );

        return http.build();
    }
}
